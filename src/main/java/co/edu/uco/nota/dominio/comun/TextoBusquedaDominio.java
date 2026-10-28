package co.edu.uco.nota.dominio.comun;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.excepcion.TipoError;
import co.edu.uco.nota.crosscutting.helper.TextoHelper;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;

public record TextoBusquedaDominio(String valor) {

	public static final int LONGITUD_MAXIMA = 100;

	public TextoBusquedaDominio {
		valor = TextoHelper.aplicarTrim(valor);
	}

	public void validar() {
		if (valor.length() > LONGITUD_MAXIMA) {
			throw NotaException.crear(TipoError.DATOS_INVALIDOS, ClaveMensaje.TEXTO_DE_BUSQUEDA_MUY_LARGO,
					"El texto de búsqueda tiene " + valor.length() + " caracteres.", LONGITUD_MAXIMA);
		}
	}

}
