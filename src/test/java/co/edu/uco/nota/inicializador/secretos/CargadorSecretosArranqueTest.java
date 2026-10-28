package co.edu.uco.nota.inicializador.secretos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Instant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.mock.env.MockEnvironment;

import co.edu.uco.nota.datosprueba.ServidorHttpPrueba;

class CargadorSecretosArranqueTest {

	private static final DeferredLogFactory LOGS = proveedor -> proveedor.get();

	private ServidorHttpPrueba servidor;
	private CargadorSecretosArranque cargador;
	private final SpringApplication aplicacion = new SpringApplication();

	@BeforeEach
	void preparar() throws IOException {
		servidor = new ServidorHttpPrueba();
		servidor.responder("/msi/token", 200, "{\"access_token\":\"t\",\"expires_on\":\""
				+ Instant.now().plusSeconds(3600).getEpochSecond() + "\"}");
		cargador = new CargadorSecretosArranque(LOGS, HttpClient.newHttpClient());
	}

	@AfterEach
	void cerrar() {
		servidor.close();
	}

	private MockEnvironment entornoConBaul() {
		return new MockEnvironment().withProperty("nota.baul.url", servidor.url())
				.withProperty("nota.azure.identidad.endpoint", servidor.url() + "/msi/token")
				.withProperty("nota.azure.identidad.encabezado", "x");
	}

	@Test
	@DisplayName("Sin baúl configurado deja la contraseña de las variables de entorno")
	void sinBaul() {
		final MockEnvironment entorno = new MockEnvironment();
		new CargadorSecretosArranque(LOGS).postProcessEnvironment(entorno, aplicacion);

		assertFalse(entorno.getPropertySources().contains(CargadorSecretosArranque.FUENTE));
	}

	@Test
	@DisplayName("Al arrancar pone la contraseña de la base que trae del baúl")
	void cargaContrasena() {
		servidor.responder("/secrets/db-password", 200, "{\"value\":\"clave-del-baul\"}");
		final MockEnvironment entorno = entornoConBaul().withProperty("spring.datasource.password", "otra");

		cargador.postProcessEnvironment(entorno, aplicacion);

		assertEquals("clave-del-baul", entorno.getProperty(CargadorSecretosArranque.PROPIEDAD_CLAVE_BASE_DATOS));
	}

	@Test
	@DisplayName("Si el baúl no entrega la contraseña, la aplicación no arranca")
	void fallaSinSecreto() {
		servidor.responder("/secrets/db-password", 403, "{}");
		final MockEnvironment entorno = entornoConBaul();

		assertThrows(IllegalStateException.class, () -> cargador.postProcessEnvironment(entorno, aplicacion));
	}

}
