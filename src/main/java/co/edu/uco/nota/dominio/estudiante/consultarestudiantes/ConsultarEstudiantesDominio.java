package co.edu.uco.nota.dominio.estudiante.consultarestudiantes;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.paginacion.Paginacion;
import co.edu.uco.nota.dominio.comun.ConsultaPaginadaDominio;
import co.edu.uco.nota.dominio.comun.TextoBusquedaDominio;

public record ConsultarEstudiantesDominio(TextoBusquedaDominio texto, ConsultaPaginadaDominio paginacion) {

	public ConsultarEstudiantesDominio {
		texto = ObjetoHelper.obtenerValorPorDefecto(texto, new TextoBusquedaDominio(null));
		paginacion = ObjetoHelper.obtenerValorPorDefecto(paginacion,
				new ConsultaPaginadaDominio(Paginacion.PAGINA_INICIAL, Paginacion.TAMANO_POR_DEFECTO));
	}

	public void validar() {
		texto.validar();
		paginacion.validar();
	}

}
