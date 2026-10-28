package co.edu.uco.nota.infraestructura.input.controladores.respuesta;

import org.springframework.stereotype.Component;

import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.servicios.input.dto.request.ObtenerMensajeRequestDTO;
import co.edu.uco.nota.servicios.input.ports.ObtenerMensajeInputPort;

@Component
public class CatalogoMensajes {

	private final ObtenerMensajeInputPort obtenerMensaje;

	public CatalogoMensajes(final ObtenerMensajeInputPort obtenerMensaje) {
		this.obtenerMensaje = obtenerMensaje;
	}

	public String obtener(final ClaveMensaje clave, final Object... parametros) {
		return obtenerMensaje.ejecutar(ObtenerMensajeRequestDTO.crear(clave, parametros));
	}

}
