package co.edu.uco.nota.crosscutting.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MensajeNumeroHelperTest {

	@Test
	@DisplayName("Formatea la plantilla con sus parámetros y tolera nulos o plantillas dañadas")
	void formateaMensajes() {
		assertEquals("Entre 0 y 5,5.", MensajeHelper.formatear("Entre {0} y {1}.", List.of(0, new BigDecimal("5.5"))));
		assertEquals("Sin datos", MensajeHelper.formatear("Sin datos", null));
		assertEquals("", MensajeHelper.formatear(null, List.of()));
		assertEquals("Mal {0", MensajeHelper.formatear("Mal {0", List.of("x")));
	}

	@Test
	@DisplayName("Convierte textos a decimales con punto o coma y usa el valor por defecto si no puede")
	void convierteDecimales() {
		assertEquals(new BigDecimal("3.5"), NumeroHelper.convertirADecimal(" 3,5 ", BigDecimal.ONE));
		assertEquals(new BigDecimal("4"), NumeroHelper.convertirADecimal("4", BigDecimal.ONE));
		assertEquals(BigDecimal.ONE, NumeroHelper.convertirADecimal("abc", BigDecimal.ONE));
		assertEquals(BigDecimal.ONE, NumeroHelper.convertirADecimal(null, BigDecimal.ONE));
	}

	@Test
	@DisplayName("Muestra los números con coma decimal y la cantidad de decimales pedida")
	void formateaNumeros() {
		assertEquals("4,30", NumeroHelper.formatearConDecimales(new BigDecimal("4.3"), 2));
		assertEquals("3,0", NumeroHelper.formatearConDecimales(new BigDecimal("3"), 1));
		assertEquals("0,00", NumeroHelper.formatearConDecimales(null, 2));
	}

}
