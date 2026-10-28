package co.edu.uco.nota.infraestructura.output.serviciosexternos.notificaciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.datosprueba.DatosPrueba;
import co.edu.uco.nota.datosprueba.RelojMovil;
import co.edu.uco.nota.datosprueba.ServiciosExternosEnMemoria;
import co.edu.uco.nota.datosprueba.ServidorHttpPrueba;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.azure.ClienteHttpAzure;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.notificaciones.NotificacionCorreo;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.secretos.BaulSecretosServicio;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

class NotificacionCorreoAcsAdapterTest {

	private static final String CLAVE = "c2VjcmV0by1kZS1wcnVlYmE=";
	private static final NotificacionCorreo AVISO = new NotificacionCorreo("ana.perez@example.com", "Ana Pérez",
			ClaveMensaje.NOTIFICACION_NOTA_REGISTRADA_ASUNTO, ClaveMensaje.NOTIFICACION_NOTA_REGISTRADA_APROBADA,
			List.of("Ana", "Parcial 1", "Ingeniería de Software 2", "4,30", "3,0"));

	private ServidorHttpPrueba servidor;

	@BeforeEach
	void preparar() throws IOException {
		servidor = new ServidorHttpPrueba();
		servidor.responder("/emails:send", 202, "{\"id\":\"1\",\"status\":\"Running\"}");
	}

	@AfterEach
	void cerrar() {
		servidor.close();
		MDC.clear();
	}

	private NotificacionCorreoAcsAdapter adaptador(final String remitente, final BaulSecretosServicio baul) {
		return new NotificacionCorreoAcsAdapter(ServiciosExternosEnMemoria.catalogoLocal(), baul,
				new ClienteHttpAzure(HttpClient.newHttpClient()), JsonMapper.shared(), Runnable::run, remitente,
				"acs-conexion", new RelojMovil(DatosPrueba.AHORA));
	}

	private BaulSecretosServicio baulConConexion() {
		return nombre -> "acs-conexion".equals(nombre)
				? Optional.of("endpoint=" + servidor.url() + "/;accesskey=" + CLAVE)
				: Optional.empty();
	}

	@Test
	@DisplayName("Envía el correo con la plantilla del catálogo y la solicitud firmada")
	void enviaCorreo() {
		MDC.put("trazabilidad", "traza-1");
		adaptador("DoNotReply@nota.azurecomm.net", baulConConexion()).notificar(AVISO);

		assertEquals(1, servidor.contar("/emails:send"));
		final ServidorHttpPrueba.Solicitud solicitud = servidor.solicitudes.get(0);
		assertEquals("POST", solicitud.metodo());
		assertEquals(NotificacionCorreoAcsAdapter.RUTA_ENVIO, solicitud.rutaYConsulta());

		final JsonNode cuerpo = JsonMapper.shared().readTree(solicitud.cuerpo());
		assertEquals("DoNotReply@nota.azurecomm.net", cuerpo.path("senderAddress").asString());
		assertEquals("Tienes una nueva nota en Ingeniería de Software 2", cuerpo.path("content").path("subject").asString());
		assertTrue(cuerpo.path("content").path("plainText").asString().contains("Parcial 1 en Ingeniería de Software 2: 4,30."));
		assertEquals("ana.perez@example.com", cuerpo.path("recipients").path("to").get(0).path("address").asString());

		final String host = URI.create(servidor.url()).getRawAuthority();
		final String resumen = FirmadorHmacAcs.resumirContenido(solicitud.cuerpo());
		assertEquals(resumen, solicitud.encabezado("x-ms-content-sha256"));
		final String firma = FirmadorHmacAcs.firmar(CLAVE, FirmadorHmacAcs.cadenaAFirmar("POST",
				solicitud.rutaYConsulta(), solicitud.encabezado("x-ms-date"), host, resumen));
		assertEquals("HMAC-SHA256 SignedHeaders=x-ms-date;host;x-ms-content-sha256&Signature=" + firma,
				solicitud.encabezado("Authorization"));
		assertEquals("traza-1", MDC.get("trazabilidad"));
	}

	@Test
	@DisplayName("Dentro de una transacción espera a que se confirme antes de enviar")
	void esperaElCommit() {
		TransactionSynchronizationManager.initSynchronization();
		try {
			adaptador("DoNotReply@nota.azurecomm.net", baulConConexion()).notificar(AVISO);
			assertEquals(0, servidor.solicitudes.size());

			TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
			assertEquals(1, servidor.contar("/emails:send"));
		} finally {
			TransactionSynchronizationManager.clearSynchronization();
		}
	}

	@Test
	@DisplayName("No envía nada si no hay remitente, conexión o destinatario, y no lanza errores")
	void noEnviaSinConfiguracion() {
		adaptador("", baulConConexion()).notificar(AVISO);
		adaptador("DoNotReply@nota.azurecomm.net", nombre -> Optional.empty()).notificar(AVISO);
		adaptador("DoNotReply@nota.azurecomm.net", nombre -> Optional.of("sin formato")).notificar(AVISO);
		adaptador("DoNotReply@nota.azurecomm.net", baulConConexion()).notificar(null);

		assertEquals(0, servidor.solicitudes.size());
	}

	@Test
	@DisplayName("Si el servicio de correo rechaza o no responde, solo deja registro")
	void toleraFallas() {
		servidor.responder("/emails:send", 400, "{\"error\":{\"code\":\"BadRequest\"}}");
		adaptador("DoNotReply@nota.azurecomm.net", baulConConexion()).notificar(AVISO);
		assertEquals(1, servidor.contar("/emails:send"));

		final String url = servidor.url();
		servidor.close();
		adaptador("DoNotReply@nota.azurecomm.net", nombre -> Optional.of("endpoint=" + url + ";accesskey=" + CLAVE))
				.notificar(AVISO);
	}

}
