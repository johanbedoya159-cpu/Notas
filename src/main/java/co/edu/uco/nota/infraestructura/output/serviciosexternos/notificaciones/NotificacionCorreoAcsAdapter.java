package co.edu.uco.nota.infraestructura.output.serviciosexternos.notificaciones;

import java.net.http.HttpResponse;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import co.edu.uco.nota.crosscutting.helper.MensajeHelper;
import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.TextoHelper;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.azure.ClienteHttpAzure;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.azure.IdentidadAzure;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.notificaciones.dto.ContenidoCorreoDTO;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.notificaciones.dto.CorreoAcsDTO;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.notificaciones.dto.DestinatariosCorreoDTO;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.notificaciones.dto.DireccionCorreoDTO;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.catalogos.CatalogoMensajesServicio;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.notificaciones.NotificacionCorreo;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.notificaciones.NotificacionServicio;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.secretos.BaulSecretosServicio;
import tools.jackson.databind.json.JsonMapper;

@Component
public class NotificacionCorreoAcsAdapter implements NotificacionServicio {

	public static final String RUTA_ENVIO = "/emails:send?api-version=2023-03-31";

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificacionCorreoAcsAdapter.class);

	private final CatalogoMensajesServicio catalogo;
	private final BaulSecretosServicio baul;
	private final ClienteHttpAzure http;
	private final JsonMapper json;
	private final Executor ejecutor;
	private final String remitente;
	private final String secreto;
	private final Clock reloj;

	public NotificacionCorreoAcsAdapter(final CatalogoMensajesServicio catalogo, final BaulSecretosServicio baul,
			final ClienteHttpAzure http, final JsonMapper json,
			@Qualifier("ejecutorNotificaciones") final Executor ejecutor,
			@Value("${nota.notificaciones.remitente:}") final String remitente,
			@Value("${nota.notificaciones.secreto:acs-conexion}") final String secreto, final Clock reloj) {
		this.catalogo = catalogo;
		this.baul = baul;
		this.http = http;
		this.json = json;
		this.ejecutor = ejecutor;
		this.remitente = TextoHelper.aplicarTrim(remitente);
		this.secreto = TextoHelper.aplicarTrim(secreto);
		this.reloj = reloj;
	}

	@Override
	public void notificar(final NotificacionCorreo notificacion) {
		if (remitente.isEmpty()) {
			LOGGER.info("Las notificaciones por correo no están configuradas; no se envía el aviso.");
			return;
		}

		final NotificacionCorreo datos = ObjetoHelper.obtenerValorPorDefecto(notificacion,
				new NotificacionCorreo(null, null, null, null, null));
		final Map<String, String> contexto = ObjetoHelper.obtenerValorPorDefecto(MDC.getCopyOfContextMap(), Map.of());
		final Runnable tarea = () -> enviarConContexto(datos, contexto);

		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					ejecutor.execute(tarea);
				}
			});
			return;
		}
		ejecutor.execute(tarea);
	}

	private void enviarConContexto(final NotificacionCorreo notificacion, final Map<String, String> contexto) {
		final Map<String, String> anterior = ObjetoHelper.obtenerValorPorDefecto(MDC.getCopyOfContextMap(), Map.of());
		MDC.setContextMap(contexto);
		try {
			enviar(notificacion);
		} catch (final RuntimeException excepcion) {
			LOGGER.warn("No se pudo enviar el correo de notificación. Causa: {}", excepcion.getMessage());
		} finally {
			MDC.setContextMap(anterior);
		}
	}

	private void enviar(final NotificacionCorreo notificacion) {
		if (notificacion.destinatario().isEmpty()) {
			LOGGER.warn("La notificación no tiene destinatario; no se envía.");
			return;
		}

		final Optional<ConexionAcs> conexion = baul.obtenerSecreto(secreto).flatMap(ConexionAcs::leer);
		if (conexion.isEmpty()) {
			LOGGER.warn("No se encontró en el baúl la conexión del servicio de correo ({}).", secreto);
			return;
		}

		final String asunto = MensajeHelper.formatear(catalogo.obtenerPlantilla(notificacion.plantillaAsunto()),
				notificacion.parametros());
		final String cuerpo = MensajeHelper.formatear(catalogo.obtenerPlantilla(notificacion.plantillaCuerpo()),
				notificacion.parametros());
		final CorreoAcsDTO correo = new CorreoAcsDTO(remitente, new ContenidoCorreoDTO(asunto, cuerpo),
				new DestinatariosCorreoDTO(
						List.of(new DireccionCorreoDTO(notificacion.destinatario(), notificacion.nombreDestinatario()))));

		final HttpResponse<String> respuesta = http.enviar(FirmadorHmacAcs.crearSolicitud(conexion.get(), RUTA_ENVIO,
				json.writeValueAsString(correo), reloj.instant(), IdentidadAzure.TIEMPO_MAXIMO));

		if (respuesta.statusCode() == 202) {
			LOGGER.info("El servicio de correo aceptó la notificación de nota registrada.");
		} else {
			LOGGER.warn("El servicio de correo rechazó la notificación con el estado {}.", respuesta.statusCode());
		}
	}

}
