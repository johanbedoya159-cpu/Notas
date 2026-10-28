package co.edu.uco.nota.crosscutting.helper;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

public final class MensajeHelper {

	private static final Locale ESPANOL_COLOMBIA = Locale.forLanguageTag("es-CO");

	private MensajeHelper() {
	}

	public static String formatear(final String plantilla, final List<?> parametros) {
		final String texto = TextoHelper.obtenerValorPorDefecto(plantilla);
		final List<?> valores = ObjetoHelper.obtenerValorPorDefecto(parametros, List.of());
		try {
			return new MessageFormat(texto, ESPANOL_COLOMBIA).format(valores.toArray());
		} catch (final IllegalArgumentException excepcion) {
			return texto;
		}
	}

}
