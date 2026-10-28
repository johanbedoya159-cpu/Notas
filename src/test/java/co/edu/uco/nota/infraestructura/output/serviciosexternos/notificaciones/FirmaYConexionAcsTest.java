package co.edu.uco.nota.infraestructura.output.serviciosexternos.notificaciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.datosprueba.DatosPrueba;

class FirmaYConexionAcsTest {

	private static final String CLAVE = "c2VjcmV0by1kZS1wcnVlYmE=";

	@Test
	@DisplayName("Lee la cadena de conexión del servicio de correo sin importar mayúsculas ni la barra final")
	void leeConexion() {
		assertEquals(Optional.of(new ConexionAcs("https://nota.communication.azure.com", CLAVE)),
				ConexionAcs.leer("Endpoint=https://nota.communication.azure.com/; AccessKey=" + CLAVE + ";otro"));
		assertEquals(Optional.empty(), ConexionAcs.leer("endpoint=https://x"));
		assertEquals(Optional.empty(), ConexionAcs.leer("accesskey=" + CLAVE));
		assertEquals(Optional.empty(), ConexionAcs.leer(null));
	}

	@Test
	@DisplayName("Arma la solicitud firmada con fecha, resumen del cuerpo y firma HMAC")
	void firmaSolicitud() {
		final ConexionAcs conexion = new ConexionAcs("https://nota.communication.azure.com", CLAVE);
		final HttpRequest solicitud = FirmadorHmacAcs.crearSolicitud(conexion, "/emails:send?api-version=2023-03-31",
				"{}", DatosPrueba.AHORA, Duration.ofSeconds(5));

		final String fecha = solicitud.headers().firstValue("x-ms-date").orElseThrow();
		final String resumen = solicitud.headers().firstValue("x-ms-content-sha256").orElseThrow();
		assertEquals("Wed, 28 Oct 2026 15:00:00 GMT", fecha);
		assertEquals("RBNvo1WzZ4oRRq0W9+hknpT7T8If536DEMBg9hyq/4o=", resumen);
		final String esperada = FirmadorHmacAcs.firmar(CLAVE, "POST\n/emails:send?api-version=2023-03-31\n" + fecha
				+ ";nota.communication.azure.com;" + resumen);
		assertEquals("HMAC-SHA256 SignedHeaders=x-ms-date;host;x-ms-content-sha256&Signature=" + esperada,
				solicitud.headers().firstValue("Authorization").orElseThrow());
		assertTrue(solicitud.uri().toString().endsWith("/emails:send?api-version=2023-03-31"));
	}

	@Test
	@DisplayName("Falla con un error propio si la clave de acceso no es válida")
	void claveInvalida() {
		assertThrows(NotaException.class, () -> FirmadorHmacAcs.firmar("no-es-base64!", "texto"));
	}

}
