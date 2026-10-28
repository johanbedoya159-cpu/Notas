package co.edu.uco.nota.servicios.output.ports.serviciosexternos.notificaciones;

import java.util.List;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.TextoHelper;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;

public record NotificacionCorreo(String destinatario, String nombreDestinatario, ClaveMensaje plantillaAsunto,
		ClaveMensaje plantillaCuerpo, List<String> parametros) {

	public NotificacionCorreo {
		destinatario = TextoHelper.aplicarTrim(destinatario);
		nombreDestinatario = TextoHelper.aplicarTrim(nombreDestinatario);
		plantillaAsunto = ObjetoHelper.obtenerValorPorDefecto(plantillaAsunto, ClaveMensaje.ERROR_INESPERADO);
		plantillaCuerpo = ObjetoHelper.obtenerValorPorDefecto(plantillaCuerpo, ClaveMensaje.ERROR_INESPERADO);
		parametros = List.copyOf(ObjetoHelper.obtenerValorPorDefecto(parametros, List.of()));
	}

}
