package co.edu.uco.nota.servicios.casosuso.mensaje;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.servicios.casosuso.mensaje.obtenermensaje.impl.ObtenerMensajeCasoUsoImpl;
import co.edu.uco.nota.servicios.input.adapters.ObtenerMensajeInteractor;
import co.edu.uco.nota.servicios.input.dto.request.ObtenerMensajeRequestDTO;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.notificaciones.NotificacionCorreo;

class ObtenerMensajeTest {

	private final List<ClaveMensaje> pedidas = new ArrayList<>();
	private final ObtenerMensajeInteractor interactor = new ObtenerMensajeInteractor(
			new ObtenerMensajeCasoUsoImpl(clave -> {
				pedidas.add(clave);
				return "Entre {0} y {1}.";
			}));

	@Test
	@DisplayName("Pide la plantilla al catálogo y la llena con los parámetros")
	void construyeMensaje() {
		assertEquals("Entre 0 y 5.",
				interactor.ejecutar(ObtenerMensajeRequestDTO.crear(ClaveMensaje.VALOR_FUERA_DE_RANGO, 0, 5)));
		assertEquals(List.of(ClaveMensaje.VALOR_FUERA_DE_RANGO), pedidas);
	}

	@Test
	@DisplayName("Sin solicitud, sin clave o con parámetros nulos usa los valores por defecto")
	void valoresPorDefecto() {
		interactor.ejecutar(null);
		interactor.ejecutar(ObtenerMensajeRequestDTO.crear(null, (Object[]) null));
		assertEquals("Entre  y {1}.", interactor.ejecutar(ObtenerMensajeRequestDTO.crear(ClaveMensaje.NOTA_REGISTRADA,
				(Object) null)));
		assertEquals(ClaveMensaje.ERROR_INESPERADO, pedidas.get(0));
		assertEquals(ClaveMensaje.ERROR_INESPERADO, pedidas.get(1));
		assertEquals("Entre {0} y {1}.", new ObtenerMensajeCasoUsoImpl(clave -> "Entre {0} y {1}.").ejecutar(null));
	}

	@Test
	@DisplayName("Exige sus dependencias y la notificación no guarda nulos")
	void dependenciasYNotificacion() {
		assertThrows(NotaException.class, () -> new ObtenerMensajeCasoUsoImpl(null));
		assertThrows(NotaException.class, () -> new ObtenerMensajeInteractor(null));

		final NotificacionCorreo vacia = new NotificacionCorreo(null, null, null, null, null);
		assertEquals("", vacia.destinatario());
		assertEquals(ClaveMensaje.ERROR_INESPERADO, vacia.plantillaCuerpo());
		assertEquals(List.of(), vacia.parametros());
	}

}
