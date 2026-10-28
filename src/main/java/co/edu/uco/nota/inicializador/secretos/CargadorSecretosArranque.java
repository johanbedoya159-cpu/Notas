package co.edu.uco.nota.inicializador.secretos;

import java.net.http.HttpClient;
import java.time.Clock;
import java.time.Duration;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import co.edu.uco.nota.crosscutting.helper.TextoHelper;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.azure.ClienteHttpAzure;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.azure.CredencialesAzure;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.azure.IdentidadAzure;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.secretos.BaulSecretosKeyVaultAdapter;
import tools.jackson.databind.json.JsonMapper;

public class CargadorSecretosArranque implements EnvironmentPostProcessor {

	public static final String FUENTE = "baulSecretosArranque";
	public static final String PROPIEDAD_CLAVE_BASE_DATOS = "spring.datasource.password";

	private final Log log;
	private final HttpClient cliente;

	public CargadorSecretosArranque(final DeferredLogFactory fabricaLogs) {
		this(fabricaLogs, HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build());
	}

	CargadorSecretosArranque(final DeferredLogFactory fabricaLogs, final HttpClient cliente) {
		this.log = fabricaLogs.getLog(CargadorSecretosArranque.class);
		this.cliente = cliente;
	}

	@Override
	public void postProcessEnvironment(final ConfigurableEnvironment entorno, final SpringApplication aplicacion) {
		final String url = TextoHelper.aplicarTrim(entorno.getProperty("nota.baul.url", ""));
		if (url.isEmpty()) {
			log.info("No hay baúl de secretos configurado; la contraseña de la base sale de las variables de entorno.");
			return;
		}

		final String nombre = TextoHelper.aplicarTrim(entorno.getProperty("nota.baul.secreto-base-datos", "db-password"));
		final ClienteHttpAzure http = new ClienteHttpAzure(cliente);
		final JsonMapper json = JsonMapper.shared();
		final Clock reloj = Clock.systemUTC();
		final IdentidadAzure identidad = new IdentidadAzure(http, json,
				new CredencialesAzure(entorno.getProperty("nota.azure.identidad.endpoint", ""),
						entorno.getProperty("nota.azure.identidad.encabezado", ""),
						entorno.getProperty("nota.azure.aplicacion.autoridad", ""),
						entorno.getProperty("nota.azure.aplicacion.inquilino", ""),
						entorno.getProperty("nota.azure.aplicacion.cliente", ""),
						entorno.getProperty("nota.azure.aplicacion.secreto", "")),
				reloj);
		final BaulSecretosKeyVaultAdapter baul = new BaulSecretosKeyVaultAdapter(identidad, http, json, url, 1,
				entorno, reloj);

		final String clave = baul.obtenerSecreto(nombre).orElseThrow(() -> new IllegalStateException(
				"No se pudo leer del baúl de secretos la contraseña de la base de datos (" + nombre + ")."));
		entorno.getPropertySources().addFirst(new MapPropertySource(FUENTE, Map.of(PROPIEDAD_CLAVE_BASE_DATOS, clave)));
		log.info("La contraseña de la base de datos se cargó desde el baúl de secretos.");
	}

}
