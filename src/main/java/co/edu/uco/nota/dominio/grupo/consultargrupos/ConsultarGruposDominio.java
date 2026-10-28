package co.edu.uco.nota.dominio.grupo.consultargrupos;

import java.util.UUID;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;
import co.edu.uco.nota.crosscutting.paginacion.Paginacion;
import co.edu.uco.nota.dominio.comun.ConsultaPaginadaDominio;
import co.edu.uco.nota.dominio.comun.TextoBusquedaDominio;

public record ConsultarGruposDominio(TextoBusquedaDominio texto, UUID docente, ConsultaPaginadaDominio paginacion) {

	public ConsultarGruposDominio {
		texto = ObjetoHelper.obtenerValorPorDefecto(texto, new TextoBusquedaDominio(null));
		docente = UUIDHelper.obtenerValorPorDefecto(docente);
		paginacion = ObjetoHelper.obtenerValorPorDefecto(paginacion,
				new ConsultaPaginadaDominio(Paginacion.PAGINA_INICIAL, Paginacion.TAMANO_POR_DEFECTO));
	}

	public void validar() {
		texto.validar();
		paginacion.validar();
	}

}
