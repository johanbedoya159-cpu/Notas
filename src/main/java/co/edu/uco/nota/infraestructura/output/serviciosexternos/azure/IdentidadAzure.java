package co.edu.uco.nota.infraestructura.output.serviciosexternos.azure;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.excepcion.TipoError;
import co.edu.uco.nota.crosscutting.helper.TextoHelper;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

public final class IdentidadAzure {

	public static final Duration TIEMPO_MAXIMO = Duration.ofSeconds(5);

	private static final String VERSION_API = "2019-08-01";
	private static final Duration MARGEN_RENOVACION = Duration.ofMinutes(5);
	private static final Duration VIGENCIA_POR_DEFECTO = Duration.ofMinutes(30);

	private final ClienteHttpAzure http;
	private final JsonMapper json;
	private final CredencialesAzure credenciales;
	private final Clock reloj;
	private final Map<String, Token> tokens = new ConcurrentHashMap<>();

	public IdentidadAzure(final ClienteHttpAzure http, final JsonMapper json, final CredencialesAzure credenciales,
			final Clock reloj) {
		this.http = http;
		this.json = json;
		this.credenciales = credenciales == null ? CredencialesAzure.administrada(null, null) : credenciales;
		this.reloj = reloj;
	}

	public IdentidadAzure(final ClienteHttpAzure http, final JsonMapper json, final String endpoint,
			final String encabezado, final Clock reloj) {
		this(http, json, CredencialesAzure.administrada(endpoint, encabezado), reloj);
	}

	public boolean estaDisponible() {
		return credenciales.usaIdentidadAdministrada() || credenciales.usaAplicacion();
	}

	public String obtenerToken(final String recurso) {
		final Instant ahora = reloj.instant();
		final Token vigente = tokens.get(recurso);
		if (vigente != null && ahora.isBefore(vigente.venceEn().minus(MARGEN_RENOVACION))) {
			return vigente.valor();
		}
		final Token nuevo = solicitar(recurso, ahora);
		tokens.put(recurso, nuevo);
		return nuevo.valor();
	}

	private Token solicitar(final String recurso, final Instant ahora) {
		if (credenciales.usaIdentidadAdministrada()) {
			return leerToken(http.enviar(solicitudIdentidadAdministrada(recurso)), recurso, ahora);
		}
		if (credenciales.usaAplicacion()) {
			return leerToken(http.enviar(solicitudAplicacion(recurso)), recurso, ahora);
		}
		throw NotaException.crear(TipoError.INESPERADO, ClaveMensaje.ERROR_INESPERADO,
				"La aplicación no tiene credenciales de Azure configuradas.");
	}

	private HttpRequest solicitudIdentidadAdministrada(final String recurso) {
		final URI uri = URI.create(credenciales.endpointIdentidad() + "?resource=" + codificar(recurso)
				+ "&api-version=" + VERSION_API);
		return HttpRequest.newBuilder(uri).timeout(TIEMPO_MAXIMO)
				.header("X-IDENTITY-HEADER", credenciales.encabezadoIdentidad()).GET().build();
	}

	private HttpRequest solicitudAplicacion(final String recurso) {
		final URI uri = URI.create(credenciales.autoridad() + "/" + codificar(credenciales.inquilino())
				+ "/oauth2/v2.0/token");
		final String formulario = "grant_type=client_credentials&client_id=" + codificar(credenciales.cliente())
				+ "&client_secret=" + codificar(credenciales.secretoCliente()) + "&scope="
				+ codificar(recurso + "/.default");
		return HttpRequest.newBuilder(uri).timeout(TIEMPO_MAXIMO)
				.header("Content-Type", "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString(formulario, StandardCharsets.UTF_8)).build();
	}

	private Token leerToken(final HttpResponse<String> respuesta, final String recurso, final Instant ahora) {
		if (respuesta.statusCode() != 200) {
			throw NotaException.crear(TipoError.INESPERADO, ClaveMensaje.ERROR_INESPERADO,
					"Azure respondió " + respuesta.statusCode() + " al pedir acceso a " + recurso + ".");
		}

		final JsonNode cuerpo = json.readTree(respuesta.body());
		final String valor = cuerpo.path("access_token").asString();
		if (TextoHelper.estaVacio(valor)) {
			throw NotaException.crear(TipoError.INESPERADO, ClaveMensaje.ERROR_INESPERADO,
					"Azure no entregó un token para " + recurso + ".");
		}
		return new Token(valor, calcularVencimiento(cuerpo, ahora));
	}

	private static Instant calcularVencimiento(final JsonNode cuerpo, final Instant ahora) {
		try {
			if (cuerpo.has("expires_in")) {
				return ahora.plusSeconds(Long.parseLong(TextoHelper.aplicarTrim(cuerpo.path("expires_in").asString())));
			}
			return Instant.ofEpochSecond(Long.parseLong(TextoHelper.aplicarTrim(cuerpo.path("expires_on").asString())));
		} catch (final NumberFormatException excepcion) {
			return ahora.plus(VIGENCIA_POR_DEFECTO);
		}
	}

	private static String codificar(final String texto) {
		return URLEncoder.encode(texto, StandardCharsets.UTF_8);
	}

	private record Token(String valor, Instant venceEn) {
	}

}
