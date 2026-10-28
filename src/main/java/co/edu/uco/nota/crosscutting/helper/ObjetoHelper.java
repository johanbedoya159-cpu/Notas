package co.edu.uco.nota.crosscutting.helper;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.excepcion.TipoError;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;

public final class ObjetoHelper {

	private ObjetoHelper() {
	}

	public static boolean esNulo(final Object objeto) {
		return objeto == null;
	}

	public static <T> T obtenerValorPorDefecto(final T objeto, final T valorPorDefecto) {
		return esNulo(objeto) ? valorPorDefecto : objeto;
	}

	public static <T> T requerirDependencia(final T dependencia, final String nombre) {
		if (esNulo(dependencia)) {
			throw NotaException.crear(TipoError.INESPERADO, ClaveMensaje.ERROR_INESPERADO,
					"No se recibió la dependencia obligatoria: " + nombre + ".");
		}
		return dependencia;
	}

}
