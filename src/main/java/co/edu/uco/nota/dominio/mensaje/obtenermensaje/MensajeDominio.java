package co.edu.uco.nota.dominio.mensaje.obtenermensaje;

import java.util.List;

import co.edu.uco.nota.crosscutting.helper.MensajeHelper;
import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.catalogos.CatalogoMensajesServicio;

public record MensajeDominio(ClaveMensaje clave, List<Object> parametros) {

	public MensajeDominio {
		clave = ObjetoHelper.obtenerValorPorDefecto(clave, ClaveMensaje.ERROR_INESPERADO);
		parametros = List.copyOf(ObjetoHelper.obtenerValorPorDefecto(parametros, List.of()));
	}

	public String construir(final CatalogoMensajesServicio catalogo) {
		return MensajeHelper.formatear(catalogo.obtenerPlantilla(clave), parametros);
	}

}
