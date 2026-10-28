package co.edu.uco.nota.infraestructura.input.controladores.errores;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.excepcion.TipoError;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.infraestructura.input.controladores.respuesta.CatalogoMensajes;
import co.edu.uco.nota.infraestructura.input.controladores.respuesta.Respuesta;

@RestControllerAdvice
public class ManejadorErrores {

	private static final Logger LOGGER = LoggerFactory.getLogger(ManejadorErrores.class);

	private static final Map<TipoError, HttpStatus> ESTADOS = Map.of(
			TipoError.DATOS_INVALIDOS, HttpStatus.BAD_REQUEST,
			TipoError.NO_ENCONTRADO, HttpStatus.NOT_FOUND,
			TipoError.REGLA_NEGOCIO, HttpStatus.UNPROCESSABLE_CONTENT,
			TipoError.CONFLICTO, HttpStatus.CONFLICT,
			TipoError.ACCESO_DENEGADO, HttpStatus.FORBIDDEN,
			TipoError.INESPERADO, HttpStatus.INTERNAL_SERVER_ERROR);

	private final CatalogoMensajes catalogo;

	public ManejadorErrores(final CatalogoMensajes catalogo) {
		this.catalogo = catalogo;
	}

	@ExceptionHandler(NotaException.class)
	public ResponseEntity<Respuesta<List<Object>>> manejar(final NotaException excepcion) {
		if (excepcion.getTipo() == TipoError.INESPERADO) {
			LOGGER.error("Error inesperado: {}", excepcion.getMensajeTecnico(), excepcion);
		} else {
			LOGGER.warn("Solicitud rechazada ({}): {}", excepcion.getTipo(), excepcion.getMensajeTecnico());
		}
		return responder(ESTADOS.getOrDefault(excepcion.getTipo(), HttpStatus.INTERNAL_SERVER_ERROR),
				catalogo.obtener(excepcion.getClaveMensajeUsuario(), excepcion.getParametros()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Respuesta<List<Object>>> manejar(final HttpMessageNotReadableException excepcion) {
		LOGGER.warn("El cuerpo de la solicitud no se pudo leer: {}", excepcion.getMostSpecificCause().getMessage());
		return responder(HttpStatus.BAD_REQUEST, catalogo.obtener(ClaveMensaje.SOLICITUD_INVALIDA));
	}

	@ExceptionHandler({ MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class })
	public ResponseEntity<Respuesta<List<Object>>> manejarParametro(final Exception excepcion) {
		LOGGER.warn("Parámetro inválido: {}", excepcion.getMessage());
		return responder(HttpStatus.BAD_REQUEST, catalogo.obtener(ClaveMensaje.PARAMETRO_INVALIDO));
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<Respuesta<List<Object>>> manejar(final NoResourceFoundException excepcion) {
		return responder(HttpStatus.NOT_FOUND, catalogo.obtener(ClaveMensaje.RECURSO_NO_ENCONTRADO));
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<Respuesta<List<Object>>> manejar(final HttpRequestMethodNotSupportedException excepcion) {
		return responder(HttpStatus.METHOD_NOT_ALLOWED, catalogo.obtener(ClaveMensaje.METODO_NO_PERMITIDO));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Respuesta<List<Object>>> manejar(final Exception excepcion) {
		LOGGER.error("Error inesperado al atender la solicitud.", excepcion);
		return responder(HttpStatus.INTERNAL_SERVER_ERROR, catalogo.obtener(ClaveMensaje.ERROR_INESPERADO));
	}

	private static ResponseEntity<Respuesta<List<Object>>> responder(final HttpStatus estado, final String mensaje) {
		return ResponseEntity.status(estado).body(Respuesta.fallida(mensaje));
	}

}
