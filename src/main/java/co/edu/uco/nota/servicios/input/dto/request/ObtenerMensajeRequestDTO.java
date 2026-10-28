package co.edu.uco.nota.servicios.input.dto.request;

import java.util.Arrays;
import java.util.List;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;

public record ObtenerMensajeRequestDTO(ClaveMensaje clave, List<Object> parametros) {

	public ObtenerMensajeRequestDTO {
		clave = ObjetoHelper.obtenerValorPorDefecto(clave, ClaveMensaje.ERROR_INESPERADO);
		parametros = List.copyOf(ObjetoHelper.obtenerValorPorDefecto(parametros, List.of()));
	}

	public static ObtenerMensajeRequestDTO crear(final ClaveMensaje clave, final Object... parametros) {
		return new ObtenerMensajeRequestDTO(clave,
				parametros == null ? List.of() : Arrays.stream(parametros).map(ObtenerMensajeRequestDTO::sinNulo).toList());
	}

	private static Object sinNulo(final Object valor) {
		return ObjetoHelper.obtenerValorPorDefecto(valor, "");
	}

}
