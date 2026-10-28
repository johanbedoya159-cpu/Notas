package co.edu.uco.nota.infraestructura.output.serviciosexternos.secretos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import co.edu.uco.nota.datosprueba.DatosPrueba;
import co.edu.uco.nota.datosprueba.RelojMovil;
import co.edu.uco.nota.datosprueba.ServidorHttpPrueba;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.azure.ClienteHttpAzure;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.azure.IdentidadAzure;
import tools.jackson.databind.json.JsonMapper;

class BaulSecretosKeyVaultAdapterTest {

	private ServidorHttpPrueba servidor;
	private RelojMovil reloj;
	private ClienteHttpAzure http;
	private IdentidadAzure identidad;

	@BeforeEach
	void preparar() throws IOException {
		servidor = new ServidorHttpPrueba();
		servidor.responder("/msi/token", 200, "{\"access_token\":\"token-baul\",\"expires_on\":\""
				+ DatosPrueba.AHORA.plus(Duration.ofHours(1)).getEpochSecond() + "\"}");
		reloj = new RelojMovil(DatosPrueba.AHORA);
		http = new ClienteHttpAzure(HttpClient.newHttpClient());
		identidad = new IdentidadAzure(http, JsonMapper.shared(), servidor.url() + "/msi/token", "x",
				reloj);
	}

	@AfterEach
	void cerrar() {
		servidor.close();
	}

	private BaulSecretosKeyVaultAdapter baul(final String url, final MockEnvironment entorno) {
		return new BaulSecretosKeyVaultAdapter(identidad, http, JsonMapper.shared(), url, 60, entorno, reloj);
	}

	@Test
	@DisplayName("Sin baúl remoto toma el secreto de las variables de entorno")
	void usaVariablesDeEntorno() {
		final BaulSecretosKeyVaultAdapter local = baul("", new MockEnvironment().withProperty("ACS_CONEXION", "abc"));

		assertFalse(local.usaBaulRemoto());
		assertEquals(Optional.of("abc"), local.obtenerSecreto("acs-conexion"));
		assertEquals(Optional.empty(), local.obtenerSecreto("db-password"));
		assertEquals(Optional.empty(), local.obtenerSecreto(" "));
		assertEquals("DB_PASSWORD", BaulSecretosKeyVaultAdapter.nombreVariable("db-password"));
	}

	@Test
	@DisplayName("Lee el secreto del Key Vault con el token y lo guarda en la caché")
	void leeDelKeyVault() {
		servidor.responder("/secrets/acs-conexion", 200, "{\"value\":\"endpoint=https://x;accesskey=y\"}");
		final BaulSecretosKeyVaultAdapter remoto = baul(servidor.url() + "/", new MockEnvironment());

		assertTrue(remoto.usaBaulRemoto());
		assertEquals(Optional.of("endpoint=https://x;accesskey=y"), remoto.obtenerSecreto("acs-conexion"));
		assertEquals(Optional.of("endpoint=https://x;accesskey=y"), remoto.obtenerSecreto("acs-conexion"));

		assertEquals(1, servidor.contar("/secrets/acs-conexion"));
		final ServidorHttpPrueba.Solicitud solicitud = servidor.solicitudes.stream()
				.filter(s -> s.rutaYConsulta().startsWith("/secrets/")).findFirst().orElseThrow();
		assertEquals("Bearer token-baul", solicitud.encabezado("Authorization"));
		assertTrue(solicitud.rutaYConsulta().endsWith("?api-version=7.4"));
	}

	@Test
	@DisplayName("Si el baúl niega el acceso o falla, no guarda nada y lo intenta de nuevo después")
	void noGuardaFallas() {
		servidor.responder("/secrets/db-password", 403, "{}");
		final BaulSecretosKeyVaultAdapter remoto = baul(servidor.url(), new MockEnvironment());

		assertEquals(Optional.empty(), remoto.obtenerSecreto("db-password"));
		servidor.responder("/secrets/db-password", 200, "{\"value\":\"clave\"}");
		assertEquals(Optional.of("clave"), remoto.obtenerSecreto("db-password"));

		servidor.responder("/msi/token", 500, "{}");
		final BaulSecretosKeyVaultAdapter sinToken = new BaulSecretosKeyVaultAdapter(
				new IdentidadAzure(http, JsonMapper.shared(), servidor.url() + "/msi/token", "x", reloj),
				http, JsonMapper.shared(), servidor.url(), 60, new MockEnvironment(), reloj);
		assertEquals(Optional.empty(), sinToken.obtenerSecreto("db-password"));
	}

}
