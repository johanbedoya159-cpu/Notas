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

class IdentidadAdministradaAzureTest {

	private ServidorHttpPrueba servidor;
	private RelojMovil reloj;
	private IdentidadAdministradaAzure identidad;

	@BeforeEach
	void preparar() throws IOException {
		servidor = new ServidorHttpPrueba();
		reloj = new RelojMovil(DatosPrueba.AHORA);
		identidad = new IdentidadAdministradaAzure(new ClienteHttpAzure(HttpClient.newHttpClient()),
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
		final IdentidadAdministradaAzure sinIdentidad = new IdentidadAdministradaAzure(null, null, " ", "", reloj);
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
		final IdentidadAdministradaAzure caida = new IdentidadAdministradaAzure(
				new ClienteHttpAzure(HttpClient.newHttpClient()), JsonMapper.shared(), url + "/msi/token", "x",
				reloj);
		assertThrows(NotaException.class, () -> caida.obtenerToken("r"));
	}

}
