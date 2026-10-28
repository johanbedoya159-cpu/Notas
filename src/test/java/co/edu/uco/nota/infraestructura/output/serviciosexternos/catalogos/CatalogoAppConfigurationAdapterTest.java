package co.edu.uco.nota.infraestructura.output.serviciosexternos.catalogos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.crosscutting.parametros.ClaveParametro;
import co.edu.uco.nota.datosprueba.DatosPrueba;
import co.edu.uco.nota.datosprueba.RelojMovil;
import co.edu.uco.nota.datosprueba.ServiciosExternosEnMemoria;
import co.edu.uco.nota.datosprueba.ServidorHttpPrueba;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.azure.ClienteHttpAzure;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.azure.IdentidadAzure;
import tools.jackson.databind.json.JsonMapper;

class CatalogoAppConfigurationAdapterTest {

	private static final String RUTA_MENSAJE = "/kv/mensajes%3Anotaestudiante.registrada";
	private static final String RUTA_PARAMETRO = "/kv/parametros%3Anota.valor.maximo";

	private ServidorHttpPrueba servidor;
	private RelojMovil reloj;
	private CatalogoAppConfigurationAdapter catalogo;

	@BeforeEach
	void preparar() throws IOException {
		servidor = new ServidorHttpPrueba();
		servidor.responder("/msi/token", 200, "{\"access_token\":\"token-catalogo\",\"expires_on\":\""
				+ DatosPrueba.AHORA.plus(Duration.ofHours(1)).getEpochSecond() + "\"}");
		reloj = new RelojMovil(DatosPrueba.AHORA);
		final ClienteHttpAzure http = new ClienteHttpAzure(HttpClient.newHttpClient());
		final JsonMapper json = JsonMapper.shared();
		catalogo = new CatalogoAppConfigurationAdapter(
				new IdentidadAzure(http, json, servidor.url() + "/msi/token", "x", reloj), http, json,
				servidor.url() + "/", 60, reloj);
	}

	@AfterEach
	void cerrar() {
		servidor.close();
	}

	@Test
	@DisplayName("Sin servicio remoto usa el catálogo local de mensajes y los parámetros por defecto")
	void usaCatalogoLocal() {
		final CatalogoAppConfigurationAdapter local = ServiciosExternosEnMemoria.catalogoLocal();

		assertFalse(local.usaServicioRemoto());
		assertEquals("La nota se registró correctamente.", local.obtenerPlantilla(ClaveMensaje.NOTA_REGISTRADA));
		assertEquals("Ocurrió un problema inesperado. Intente de nuevo más tarde.", local.obtenerPlantilla(null));
		assertEquals("5", local.obtenerValor(ClaveParametro.NOTA_VALOR_MAXIMO));
		assertEquals("5", local.obtenerValor(null));
	}

	@Test
	@DisplayName("Lee el mensaje del catálogo remoto con el token y lo guarda en la caché")
	void leeRemotoConCache() {
		servidor.responder(RUTA_MENSAJE, 200, "{\"key\":\"mensajes:notaestudiante.registrada\",\"value\":\"¡Listo!\"}");

		assertTrue(catalogo.usaServicioRemoto());
		assertEquals("¡Listo!", catalogo.obtenerPlantilla(ClaveMensaje.NOTA_REGISTRADA));
		assertEquals("¡Listo!", catalogo.obtenerPlantilla(ClaveMensaje.NOTA_REGISTRADA));

		assertEquals(1, servidor.contar(RUTA_MENSAJE));
		final ServidorHttpPrueba.Solicitud solicitud = servidor.solicitudes.stream()
				.filter(s -> s.rutaYConsulta().startsWith(RUTA_MENSAJE)).findFirst().orElseThrow();
		assertEquals("Bearer token-catalogo", solicitud.encabezado("Authorization"));
		assertTrue(solicitud.rutaYConsulta().endsWith("?api-version=1.0"));
	}

	@Test
	@DisplayName("Vuelve a consultar cuando vence la caché o cuando se invalida")
	void refrescaCache() {
		servidor.responder(RUTA_PARAMETRO, 200, "{\"value\":\"4\"}");
		assertEquals("4", catalogo.obtenerValor(ClaveParametro.NOTA_VALOR_MAXIMO));

		servidor.responder(RUTA_PARAMETRO, 200, "{\"value\":\"4.5\"}");
		assertEquals("4", catalogo.obtenerValor(ClaveParametro.NOTA_VALOR_MAXIMO));
		reloj.avanzar(Duration.ofSeconds(61));
		assertEquals("4.5", catalogo.obtenerValor(ClaveParametro.NOTA_VALOR_MAXIMO));

		servidor.responder(RUTA_PARAMETRO, 200, "{\"value\":\"5\"}");
		catalogo.invalidarCache();
		assertEquals("5", catalogo.obtenerValor(ClaveParametro.NOTA_VALOR_MAXIMO));
	}

	@Test
	@DisplayName("Si la clave no existe o el servicio falla, usa el valor local")
	void usaRespaldo() {
		assertEquals("La nota se registró correctamente.", catalogo.obtenerPlantilla(ClaveMensaje.NOTA_REGISTRADA));

		servidor.responder("/kv/parametros", 503, "{}");
		assertEquals("0", catalogo.obtenerValor(ClaveParametro.NOTA_VALOR_MINIMO));

		servidor.responder("/msi/token", 500, "{}");
		final CatalogoAppConfigurationAdapter sinToken = new CatalogoAppConfigurationAdapter(
				new IdentidadAzure(new ClienteHttpAzure(HttpClient.newHttpClient()), JsonMapper.shared(),
						servidor.url() + "/msi/token", "x", reloj),
				new ClienteHttpAzure(HttpClient.newHttpClient()), JsonMapper.shared(), servidor.url(), 0, reloj);
		assertEquals("3", sinToken.obtenerValor(ClaveParametro.NOTA_VALOR_APROBATORIO));
	}

}
