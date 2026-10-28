package co.edu.uco.nota.crosscutting.helper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.excepcion.TipoError;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.crosscutting.paginacion.Pagina;
import co.edu.uco.nota.crosscutting.paginacion.Paginacion;

class HelpersTest {

	@Test
	@DisplayName("Genera UUID versión 7, ordenados en el tiempo y distintos")
	void generaUuidVersion7() {
		final UUID primero = UUIDHelper.generarNuevo();
		final UUID segundo = UUIDHelper.generarNuevo();

		assertEquals(7, primero.version());
		assertEquals(2, primero.variant());
		assertNotEquals(primero, segundo);
		assertTrue(Math.abs(System.currentTimeMillis() - (primero.getMostSignificantBits() >>> 16)) < 5_000);
	}

	@Test
	@DisplayName("Convierte textos inválidos o nulos al UUID por defecto")
	void convierteUuid() {
		assertTrue(UUIDHelper.esPorDefecto(null));
		assertTrue(UUIDHelper.esPorDefecto(UUIDHelper.convertir("no-es-uuid")));
		assertTrue(UUIDHelper.esPorDefecto(UUIDHelper.convertir(null)));
		final UUID valor = UUIDHelper.generarNuevo();
		assertEquals(valor, UUIDHelper.convertir(" " + valor + " "));
		assertFalse(UUIDHelper.esPorDefecto(valor));
	}

	@Test
	@DisplayName("Limpia textos y aplica valores por defecto")
	void limpiaTextos() {
		assertEquals("", TextoHelper.aplicarTrim(null));
		assertEquals("hola", TextoHelper.aplicarTrim("  hola "));
		assertTrue(TextoHelper.estaVacio("   "));
		assertEquals("b", ObjetoHelper.obtenerValorPorDefecto(null, "b"));
		assertEquals("a", ObjetoHelper.obtenerValorPorDefecto("a", "b"));
	}

	@Test
	@DisplayName("Falla con error inesperado si falta una dependencia")
	void exigeDependencia() {
		final NotaException error = assertThrows(NotaException.class,
				() -> ObjetoHelper.requerirDependencia(null, "el reloj"));

		assertEquals(TipoError.INESPERADO, error.getTipo());
		assertEquals(ClaveMensaje.ERROR_INESPERADO, error.getClaveMensajeUsuario());
		assertTrue(error.getMensajeTecnico().contains("el reloj"));
	}

	@Test
	@DisplayName("La excepción conserva tipo, parámetros y causa raíz")
	void excepcionConservaDatos() {
		final IllegalStateException causa = new IllegalStateException("base caída");
		final NotaException conCausa = NotaException.crear(TipoError.CONFLICTO, ClaveMensaje.NOTA_YA_REGISTRADA,
				"duplicada", causa);
		final NotaException conParametros = NotaException.crear(null, null, null, 5, "x");

		assertEquals(causa, conCausa.getCause());
		assertEquals(TipoError.INESPERADO, conParametros.getTipo());
		assertEquals(ClaveMensaje.ERROR_INESPERADO, conParametros.getClaveMensajeUsuario());
		assertEquals("", conParametros.getMensajeTecnico());
		assertArrayEquals(new Object[] { 5, "x" }, conParametros.getParametros());
		assertEquals("notaestudiante.ya-registrada", ClaveMensaje.NOTA_YA_REGISTRADA.getClave());
	}

	@Test
	@DisplayName("La página calcula el total de páginas y convierte su contenido")
	void calculaPaginas() {
		final Pagina<Integer> pagina = new Pagina<>(List.of(1, 2), 0, 2, 5);

		assertEquals(3, pagina.totalPaginas());
		assertEquals(List.of("1", "2"), pagina.convertir(String::valueOf).contenido());
		assertFalse(Pagina.vacia(Paginacion.porDefecto()).tieneElementos());
		assertEquals(0, new Pagina<>(null, 0, 0, 0).totalPaginas());
	}

}
