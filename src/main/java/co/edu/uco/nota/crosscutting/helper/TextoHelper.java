package co.edu.uco.nota.crosscutting.helper;

public final class TextoHelper {

	public static final String VACIO = "";

	private TextoHelper() {
	}

	public static String obtenerValorPorDefecto(final String texto) {
		return ObjetoHelper.obtenerValorPorDefecto(texto, VACIO);
	}

	public static String aplicarTrim(final String texto) {
		return obtenerValorPorDefecto(texto).trim();
	}

	public static boolean estaVacio(final String texto) {
		return aplicarTrim(texto).isEmpty();
	}

}
