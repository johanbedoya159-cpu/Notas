package co.edu.uco.nota.dominio.notaestudiante.registrarnotaestudiante;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;

class NotaEstudianteDominioTest {

	private static int escalar(final String valor) {
		final NotaEstudianteDominio nota = new NotaEstudianteDominio(new BigDecimal(valor));
		nota.validarValor();
		return nota.obtenerValorEscalado();
	}

	@Test
	@DisplayName("Escala la nota multiplicando por cien")
	void escalaNota() {
		assertEquals(435, escalar("4.35"));
		assertEquals(310, escalar("3.10"));
		assertEquals(500, escalar("5"));
		assertEquals(0, escalar("0.00"));
		assertEquals(250, escalar("2.500"));
	}

	@Test
	@DisplayName("No cuenta los ceros a la derecha como decimales de más")
	void ignoraCerosALaDerecha() {
		assertEquals(400, escalar("4.0000"));
	}

	@Test
	@DisplayName("Rechaza tres decimales significativos")
	void rechazaTresDecimales() {
		final NotaEstudianteDominio nota = new NotaEstudianteDominio(new BigDecimal("1.001"));
		assertEquals(ClaveMensaje.VALOR_CON_MAS_DE_DOS_DECIMALES,
				assertThrows(NotaException.class, nota::validarValor).getClaveMensajeUsuario());
	}

}
