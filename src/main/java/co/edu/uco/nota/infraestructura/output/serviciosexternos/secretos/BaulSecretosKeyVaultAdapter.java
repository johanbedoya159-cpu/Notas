package co.edu.uco.nota.infraestructura.output.serviciosexternos.secretos;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import co.edu.uco.nota.crosscutting.helper.TextoHelper;
import co.edu.uco.nota.infraestructura.output.cache.CacheEnMemoria;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.azure.ClienteHttpAzure;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.azure.IdentidadAzure;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.secretos.BaulSecretosServicio;
import tools.jackson.databind.json.JsonMapper;

@Component
public class BaulSecretosKeyVaultAdapter implements BaulSecretosServicio {

	public static final String RECURSO = "https://vault.azure.net";

	private static final Logger LOGGER = LoggerFactory.getLogger(BaulSecretosKeyVaultAdapter.class);
	private static final String VERSION_API = "7.4";
	private static final int CAPACIDAD_CACHE = 50;

	private final IdentidadAzure identidad;
	private final ClienteHttpAzure http;
	private final JsonMapper json;
	private final String url;
	private final Environment entorno;
	private final CacheEnMemoria<String> cache;

	public BaulSecretosKeyVaultAdapter(final IdentidadAzure identidad, final ClienteHttpAzure http,
			final JsonMapper json, @Value("${nota.baul.url:}") final String url,
			@Value("${nota.baul.cache-minutos:60}") final long minutosCache, final Environment entorno,
			final Clock reloj) {
		this.identidad = identidad;
		this.http = http;
		this.json = json;
		final String limpia = TextoHelper.aplicarTrim(url);
		this.url = limpia.endsWith("/") ? limpia.substring(0, limpia.length() - 1) : limpia;
		this.entorno = entorno;
		this.cache = new CacheEnMemoria<>("secretos", Duration.ofMinutes(Math.max(1, minutosCache)), CAPACIDAD_CACHE,
				reloj);
	}

	@Override
	public Optional<String> obtenerSecreto(final String nombre) {
		final String solicitado = TextoHelper.aplicarTrim(nombre);
		if (solicitado.isEmpty()) {
			return Optional.empty();
		}
		final String valor = cache.obtener(solicitado, () -> consultar(solicitado), texto -> !texto.isEmpty());
		return valor.isEmpty() ? Optional.empty() : Optional.of(valor);
	}

	public boolean usaBaulRemoto() {
		return !url.isEmpty();
	}

	private String consultar(final String nombre) {
		if (!usaBaulRemoto()) {
			return TextoHelper.aplicarTrim(entorno.getProperty(nombreVariable(nombre), ""));
		}

		try {
			final URI uri = URI.create(url + "/secrets/" + URLEncoder.encode(nombre, StandardCharsets.UTF_8)
					+ "?api-version=" + VERSION_API);
			final HttpResponse<String> respuesta = http.enviar(HttpRequest.newBuilder(uri)
					.timeout(IdentidadAzure.TIEMPO_MAXIMO)
					.header("Authorization", "Bearer " + identidad.obtenerToken(RECURSO)).GET().build());

			if (respuesta.statusCode() == 200) {
				return TextoHelper.aplicarTrim(json.readTree(respuesta.body()).path("value").asString());
			}
			LOGGER.warn("El baúl de secretos respondió {} al pedir el secreto {}.", respuesta.statusCode(), nombre);
		} catch (final RuntimeException excepcion) {
			LOGGER.warn("No se pudo leer el secreto {} del baúl. Causa: {}", nombre, excepcion.getMessage());
		}
		return TextoHelper.VACIO;
	}

	static String nombreVariable(final String nombre) {
		return nombre.replace('-', '_').toUpperCase(Locale.ROOT);
	}

}
