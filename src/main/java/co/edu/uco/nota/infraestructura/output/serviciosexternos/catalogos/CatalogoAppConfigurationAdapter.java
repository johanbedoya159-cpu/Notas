package co.edu.uco.nota.infraestructura.output.serviciosexternos.catalogos;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.TextoHelper;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.crosscutting.parametros.ClaveParametro;
import co.edu.uco.nota.infraestructura.output.cache.CacheEnMemoria;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.azure.ClienteHttpAzure;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.azure.IdentidadAzure;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.catalogos.CatalogoMensajesServicio;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.catalogos.CatalogoParametrosServicio;
import tools.jackson.databind.json.JsonMapper;

@Component
public class CatalogoAppConfigurationAdapter implements CatalogoMensajesServicio, CatalogoParametrosServicio {

	public static final String RECURSO = "https://azconfig.io";
	public static final String PREFIJO_MENSAJES = "mensajes:";
	public static final String PREFIJO_PARAMETROS = "parametros:";

	private static final Logger LOGGER = LoggerFactory.getLogger(CatalogoAppConfigurationAdapter.class);
	private static final String ARCHIVO_MENSAJES = "mensajes/mensajes.properties";
	private static final String VERSION_API = "1.0";
	private static final int CAPACIDAD_CACHE = 500;

	private final IdentidadAzure identidad;
	private final ClienteHttpAzure http;
	private final JsonMapper json;
	private final String endpoint;
	private final CacheEnMemoria<String> cache;
	private final Properties mensajesLocales;

	public CatalogoAppConfigurationAdapter(final IdentidadAzure identidad, final ClienteHttpAzure http,
			final JsonMapper json, @Value("${nota.catalogo.endpoint:}") final String endpoint,
			@Value("${nota.catalogo.cache-segundos:300}") final long segundosCache, final Clock reloj) {
		this.identidad = identidad;
		this.http = http;
		this.json = json;
		this.endpoint = quitarBarraFinal(TextoHelper.aplicarTrim(endpoint));
		this.cache = new CacheEnMemoria<>("catálogo", Duration.ofSeconds(Math.max(1, segundosCache)), CAPACIDAD_CACHE,
				reloj);
		this.mensajesLocales = cargarMensajesLocales();
	}

	@Override
	public String obtenerPlantilla(final ClaveMensaje clave) {
		final ClaveMensaje solicitada = ObjetoHelper.obtenerValorPorDefecto(clave, ClaveMensaje.ERROR_INESPERADO);
		final String claveRemota = PREFIJO_MENSAJES + solicitada.getClave();
		return cache.obtener(claveRemota, () -> consultarRemoto(claveRemota)
				.orElseGet(() -> mensajesLocales.getProperty(solicitada.getClave(), solicitada.getClave())));
	}

	@Override
	public String obtenerValor(final ClaveParametro clave) {
		final ClaveParametro solicitada = ObjetoHelper.obtenerValorPorDefecto(clave, ClaveParametro.NOTA_VALOR_MAXIMO);
		final String claveRemota = PREFIJO_PARAMETROS + solicitada.getClave();
		return cache.obtener(claveRemota,
				() -> consultarRemoto(claveRemota).orElseGet(solicitada::getValorPorDefecto));
	}

	public boolean usaServicioRemoto() {
		return !TextoHelper.estaVacio(endpoint) && identidad.estaDisponible();
	}

	public void invalidarCache() {
		cache.invalidar();
	}

	private Optional<String> consultarRemoto(final String clave) {
		if (!usaServicioRemoto()) {
			return Optional.empty();
		}

		try {
			final URI uri = URI.create(endpoint + "/kv/" + URLEncoder.encode(clave, StandardCharsets.UTF_8)
					+ "?api-version=" + VERSION_API);
			final HttpResponse<String> respuesta = http.enviar(HttpRequest.newBuilder(uri)
					.timeout(IdentidadAzure.TIEMPO_MAXIMO)
					.header("Authorization", "Bearer " + identidad.obtenerToken(RECURSO))
					.header("Accept", "application/vnd.microsoft.appconfig.kv+json").GET().build());

			if (respuesta.statusCode() == 200) {
				return Optional.of(json.readTree(respuesta.body()).path("value").asString());
			}
			if (respuesta.statusCode() != 404) {
				LOGGER.warn("El catálogo respondió {} para {}; se usa el valor local.", respuesta.statusCode(), clave);
			}
		} catch (final RuntimeException excepcion) {
			LOGGER.warn("No se pudo consultar {} en el catálogo; se usa el valor local. Causa: {}", clave,
					excepcion.getMessage());
		}
		return Optional.empty();
	}

	private static Properties cargarMensajesLocales() {
		final Properties propiedades = new Properties();
		final InputStream archivo = CatalogoAppConfigurationAdapter.class.getClassLoader()
				.getResourceAsStream(ARCHIVO_MENSAJES);
		if (archivo == null) {
			LOGGER.warn("No se encontró el catálogo local de mensajes {}.", ARCHIVO_MENSAJES);
			return propiedades;
		}
		try (Reader lector = new InputStreamReader(archivo, StandardCharsets.UTF_8)) {
			propiedades.load(lector);
		} catch (final IOException excepcion) {
			LOGGER.warn("No se pudo leer el catálogo local de mensajes: {}", excepcion.getMessage());
		}
		return propiedades;
	}

	private static String quitarBarraFinal(final String texto) {
		return texto.endsWith("/") ? texto.substring(0, texto.length() - 1) : texto;
	}

}
