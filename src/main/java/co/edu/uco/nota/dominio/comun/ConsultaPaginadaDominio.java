package co.edu.uco.nota.dominio.comun;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.excepcion.TipoError;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.crosscutting.paginacion.Paginacion;

public record ConsultaPaginadaDominio(int pagina, int tamano) {

	public void validar() {
		if (pagina < Paginacion.PAGINA_INICIAL) {
			throw NotaException.crear(TipoError.DATOS_INVALIDOS, ClaveMensaje.PAGINA_INVALIDA,
					"Se pidió la página " + pagina + ".");
		}

		if (tamano < 1 || tamano > Paginacion.TAMANO_MAXIMO) {
			throw NotaException.crear(TipoError.DATOS_INVALIDOS, ClaveMensaje.TAMANO_DE_PAGINA_INVALIDO,
					"Se pidió un tamaño de página de " + tamano + ".", Paginacion.TAMANO_MAXIMO);
		}
	}

	public Paginacion aPaginacion() {
		return new Paginacion(pagina, tamano);
	}

}
