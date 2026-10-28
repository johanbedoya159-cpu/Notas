package co.edu.uco.nota.crosscutting.helper;

import java.math.BigDecimal;
import java.util.Locale;

public final class NumeroHelper {

	private static final Locale ESPANOL_COLOMBIA = Locale.forLanguageTag("es-CO");

	private NumeroHelper() {
	}

	public static BigDecimal convertirADecimal(final String texto, final BigDecimal valorPorDefecto) {
		try {
			return new BigDecimal(TextoHelper.aplicarTrim(texto).replace(',', '.'));
		} catch (final NumberFormatException excepcion) {
			return valorPorDefecto;
		}
	}

	public static String formatearConDecimales(final BigDecimal valor, final int decimales) {
		return String.format(ESPANOL_COLOMBIA, "%." + decimales + "f",
				ObjetoHelper.obtenerValorPorDefecto(valor, BigDecimal.ZERO));
	}

}
