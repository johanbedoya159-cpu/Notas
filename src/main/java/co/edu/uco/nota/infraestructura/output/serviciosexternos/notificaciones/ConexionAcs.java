package co.edu.uco.nota.infraestructura.output.serviciosexternos.notificaciones;

import java.util.Locale;
import java.util.Optional;

import co.edu.uco.nota.crosscutting.helper.TextoHelper;

public record ConexionAcs(String endpoint, String claveAcceso) {

	public static Optional<ConexionAcs> leer(final String cadenaConexion) {
		String endpoint = TextoHelper.VACIO;
		String clave = TextoHelper.VACIO;
		for (final String parte : TextoHelper.aplicarTrim(cadenaConexion).split(";")) {
			final int separador = parte.indexOf('=');
			if (separador <= 0) {
				continue;
			}
			final String nombre = parte.substring(0, separador).trim().toLowerCase(Locale.ROOT);
			final String valor = parte.substring(separador + 1).trim();
			if ("endpoint".equals(nombre)) {
				endpoint = valor.endsWith("/") ? valor.substring(0, valor.length() - 1) : valor;
			} else if ("accesskey".equals(nombre)) {
				clave = valor;
			}
		}
		return endpoint.isEmpty() || clave.isEmpty() ? Optional.empty() : Optional.of(new ConexionAcs(endpoint, clave));
	}

}
