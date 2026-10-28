package co.edu.uco.nota.inicializador.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.edu.uco.nota.servicios.casosuso.mensaje.obtenermensaje.ObtenerMensajeCasoUso;
import co.edu.uco.nota.servicios.casosuso.mensaje.obtenermensaje.impl.ObtenerMensajeCasoUsoImpl;
import co.edu.uco.nota.servicios.input.adapters.ObtenerMensajeInteractor;
import co.edu.uco.nota.servicios.input.ports.ObtenerMensajeInputPort;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.catalogos.CatalogoMensajesServicio;

@Configuration
public class MensajesConfiguracion {

	@Bean
	ObtenerMensajeCasoUso obtenerMensajeCasoUso(final CatalogoMensajesServicio catalogo) {
		return new ObtenerMensajeCasoUsoImpl(catalogo);
	}

	@Bean
	ObtenerMensajeInputPort obtenerMensajeInputPort(final ObtenerMensajeCasoUso casoUso) {
		return new ObtenerMensajeInteractor(casoUso);
	}

}
