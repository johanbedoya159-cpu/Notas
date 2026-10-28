package co.edu.uco.nota.servicios.input.adapters;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.dominio.mensaje.obtenermensaje.MensajeDominio;
import co.edu.uco.nota.servicios.casosuso.mensaje.obtenermensaje.ObtenerMensajeCasoUso;
import co.edu.uco.nota.servicios.input.dto.request.ObtenerMensajeRequestDTO;
import co.edu.uco.nota.servicios.input.ports.ObtenerMensajeInputPort;

public final class ObtenerMensajeInteractor implements ObtenerMensajeInputPort {

	private final ObtenerMensajeCasoUso casoUso;

	public ObtenerMensajeInteractor(final ObtenerMensajeCasoUso casoUso) {
		this.casoUso = ObjetoHelper.requerirDependencia(casoUso, "el caso de uso de obtener mensaje");
	}

	@Override
	public String ejecutar(final ObtenerMensajeRequestDTO solicitud) {
		final ObtenerMensajeRequestDTO datos = ObjetoHelper.obtenerValorPorDefecto(solicitud,
				new ObtenerMensajeRequestDTO(null, null));
		return casoUso.ejecutar(new MensajeDominio(datos.clave(), datos.parametros()));
	}

}
