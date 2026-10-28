package co.edu.uco.nota.infraestructura.input.controladores.respuesta;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;

@Component
public class CatalogoMensajes {

	private static final Locale ESPANOL_COLOMBIA = Locale.forLanguageTag("es-CO");

	private final MessageSource fuente;

	public CatalogoMensajes(final MessageSource fuente) {
		this.fuente = fuente;
	}

	public String obtener(final ClaveMensaje clave, final Object... parametros) {
		return fuente.getMessage(clave.getClave(), parametros, clave.getClave(), ESPANOL_COLOMBIA);
	}

}
