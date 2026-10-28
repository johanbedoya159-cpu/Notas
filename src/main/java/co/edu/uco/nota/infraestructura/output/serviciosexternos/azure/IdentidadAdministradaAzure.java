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

public final class IdentidadAdministradaAzure {

	public static final Duration TIEMPO_MAXIMO = Duration.ofSeconds(5);

	private static final String VERSION_API = "2019-08-01";
	private static final Duration MARGEN_RENOVACION = Duration.ofMinutes(5);
	private static final Duration VIGENCIA_POR_DEFECTO = Duration.ofMinutes(30);

	private final ClienteHttpAzure http;
	private final JsonMapper json;
	private final String endpoint;
	private final String encabezado;
	private final Clock reloj;
	private final Map<String, Token> tokens = new ConcurrentHashMap<>();

	public IdentidadAdministradaAzure(final ClienteHttpAzure http, final JsonMapper json, final String endpoint,
			final String encabezado, final Clock reloj) {
		this.http = http;
		this.json = json;
		this.endpoint = TextoHelper.aplicarTrim(endpoint);
		this.encabezado = TextoHelper.aplicarTrim(encabezado);
		this.reloj = reloj;
	}

	public boolean estaDisponible() {
		return !TextoHelper.estaVacio(endpoint) && !TextoHelper.estaVacio(encabezado);
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
		if (!estaDisponible()) {
			throw NotaException.crear(TipoError.INESPERADO, ClaveMensaje.ERROR_INESPERADO,
					"La aplicación no tiene una identidad administrada de Azure.");
		}

		final URI uri = URI.create(endpoint + "?resource=" + URLEncoder.encode(recurso, StandardCharsets.UTF_8)
				+ "&api-version=" + VERSION_API);
		final HttpResponse<String> respuesta = http.enviar(HttpRequest.newBuilder(uri).timeout(TIEMPO_MAXIMO)
				.header("X-IDENTITY-HEADER", encabezado).GET().build());

		if (respuesta.statusCode() != 200) {
			throw NotaException.crear(TipoError.INESPERADO, ClaveMensaje.ERROR_INESPERADO,
					"La identidad administrada respondió " + respuesta.statusCode() + " al pedir acceso a " + recurso
							+ ".");
		}

		final JsonNode cuerpo = json.readTree(respuesta.body());
		final String valor = cuerpo.path("access_token").asString();
		if (TextoHelper.estaVacio(valor)) {
			throw NotaException.crear(TipoError.INESPERADO, ClaveMensaje.ERROR_INESPERADO,
					"La identidad administrada no entregó un token para " + recurso + ".");
		}
		return new Token(valor, calcularVencimiento(cuerpo.path("expires_on").asString(), ahora));
	}

	private static Instant calcularVencimiento(final String segundos, final Instant ahora) {
		try {
			return Instant.ofEpochSecond(Long.parseLong(TextoHelper.aplicarTrim(segundos)));
		} catch (final NumberFormatException excepcion) {
			return ahora.plus(VIGENCIA_POR_DEFECTO);
		}
	}

	private record Token(String valor, Instant venceEn) {
	}

}
