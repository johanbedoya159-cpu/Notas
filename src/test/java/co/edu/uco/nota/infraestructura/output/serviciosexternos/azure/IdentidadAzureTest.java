package co.edu.uco.nota.infraestructura.output.serviciosexternos.azure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.datosprueba.DatosPrueba;
import co.edu.uco.nota.datosprueba.RelojMovil;
import co.edu.uco.nota.datosprueba.ServidorHttpPrueba;
import tools.jackson.databind.json.JsonMapper;

class IdentidadAzureTest {

	private ServidorHttpPrueba servidor;
	private RelojMovil reloj;
	private IdentidadAzure identidad;

	@BeforeEach
	void preparar() throws IOException {
		servidor = new ServidorHttpPrueba();
		reloj = new RelojMovil(DatosPrueba.AHORA);
		identidad = new IdentidadAzure(new ClienteHttpAzure(HttpClient.newHttpClient()),
				JsonMapper.shared(), servidor.url() + "/msi/token", "secreto-local", reloj);
	}

	@AfterEach
	void cerrar() {
		servidor.close();
	}

	private void responderToken(final String token, final long venceEn) {
		servidor.responder("/msi/token", 200,
				"{\"access_token\":\"" + token + "\",\"expires_on\":\"" + venceEn + "\"}");
	}

	@Test
	@DisplayName("Pide el token con el encabezado de la identidad y lo reutiliza mientras está vigente")
	void reutilizaToken() {
		responderToken("t1", DatosPrueba.AHORA.plus(Duration.ofHours(1)).getEpochSecond());

		assertEquals("t1", identidad.obtenerToken("https://vault.azure.net"));
		assertEquals("t1", identidad.obtenerToken("https://vault.azure.net"));

		assertEquals(1, servidor.contar("/msi/token"));
		final ServidorHttpPrueba.Solicitud solicitud = servidor.solicitudes.get(0);
		assertEquals("secreto-local", solicitud.encabezado("X-IDENTITY-HEADER"));
		assertTrue(solicitud.rutaYConsulta().contains("resource=https%3A%2F%2Fvault.azure.net"));
		assertTrue(solicitud.rutaYConsulta().endsWith("api-version=2019-08-01"));
	}

	@Test
	@DisplayName("Renueva el token cuando está por vencer y tolera un vencimiento ilegible")
	void renuevaToken() {
		servidor.responder("/msi/token", 200, "{\"access_token\":\"t1\",\"expires_on\":\"pronto\"}");
		identidad.obtenerToken("r");

		reloj.avanzar(Duration.ofMinutes(26));
		responderToken("t2", 0);
		assertEquals("t2", identidad.obtenerToken("r"));
		assertEquals(2, servidor.contar("/msi/token"));
	}

	@Test
	@DisplayName("Falla con un error propio si no hay identidad, si responde mal o si no trae token")
	void fallas() {
		final IdentidadAzure sinIdentidad = new IdentidadAzure(null, null, " ", "", reloj);
		assertFalse(sinIdentidad.estaDisponible());
		assertThrows(NotaException.class, () -> sinIdentidad.obtenerToken("r"));

		servidor.responder("/msi/token", 500, "{}");
		assertThrows(NotaException.class, () -> identidad.obtenerToken("r"));

		servidor.responder("/msi/token", 200, "{\"expires_on\":\"1\"}");
		assertThrows(NotaException.class, () -> identidad.obtenerToken("r"));
	}

	@Test
	@DisplayName("Convierte la falla de red en un error propio")
	void fallaDeRed() {
		final String url = servidor.url();
		servidor.close();
		final IdentidadAzure caida = new IdentidadAzure(
				new ClienteHttpAzure(HttpClient.newHttpClient()), JsonMapper.shared(), url + "/msi/token", "x",
				reloj);
		assertThrows(NotaException.class, () -> caida.obtenerToken("r"));
	}

	@Test
	@DisplayName("Con credenciales de aplicación pide el token a Microsoft Entra con el flujo de cliente")
	void usaCredencialesDeAplicacion() {
		servidor.responder("/inquilino-1/oauth2/v2.0/token", 200, "{\"access_token\":\"t-app\",\"expires_in\":3599}");
		final IdentidadAzure aplicacion = new IdentidadAzure(new ClienteHttpAzure(HttpClient.newHttpClient()),
				JsonMapper.shared(), CredencialesAzure.aplicacion(servidor.url() + "/", "inquilino-1", "cliente-1",
						"secreto&1"),
				reloj);

		assertTrue(aplicacion.estaDisponible());
		assertEquals("t-app", aplicacion.obtenerToken("https://vault.azure.net"));
		assertEquals("t-app", aplicacion.obtenerToken("https://vault.azure.net"));

		assertEquals(1, servidor.contar("/inquilino-1/oauth2/v2.0/token"));
		final ServidorHttpPrueba.Solicitud solicitud = servidor.solicitudes.get(0);
		assertEquals("POST", solicitud.metodo());
		assertEquals("application/x-www-form-urlencoded", solicitud.encabezado("Content-Type"));
		assertEquals("grant_type=client_credentials&client_id=cliente-1&client_secret=secreto%261"
				+ "&scope=https%3A%2F%2Fvault.azure.net%2F.default", solicitud.cuerpo());
	}

	@Test
	@DisplayName("Las credenciales limpian espacios, usan la autoridad pública por defecto y exigen todos los datos")
	void credenciales() {
		final CredencialesAzure incompletas = CredencialesAzure.aplicacion(null, " t ", "c", "");
		assertEquals(CredencialesAzure.AUTORIDAD_POR_DEFECTO, incompletas.autoridad());
		assertEquals("t", incompletas.inquilino());
		assertFalse(incompletas.usaAplicacion());
		assertFalse(incompletas.usaIdentidadAdministrada());
		assertFalse(new IdentidadAzure(null, null, incompletas, reloj).estaDisponible());
		assertFalse(new IdentidadAzure(null, null, (CredencialesAzure) null, reloj).estaDisponible());
		assertTrue(CredencialesAzure.administrada("http://x", "y").usaIdentidadAdministrada());
	}

}
