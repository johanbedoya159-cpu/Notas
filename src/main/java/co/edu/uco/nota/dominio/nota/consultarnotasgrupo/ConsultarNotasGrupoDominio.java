package co.edu.uco.nota.dominio.nota.consultarnotasgrupo;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.paginacion.Paginacion;
import co.edu.uco.nota.dominio.comun.ConsultaPaginadaDominio;
import co.edu.uco.nota.dominio.comun.GrupoExistenteDominio;
import co.edu.uco.nota.servicios.output.ports.repositorios.GrupoRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;

public record ConsultarNotasGrupoDominio(GrupoExistenteDominio grupo, ConsultaPaginadaDominio paginacion) {

	public ConsultarNotasGrupoDominio {
		grupo = ObjetoHelper.obtenerValorPorDefecto(grupo, new GrupoExistenteDominio(null));
		paginacion = ObjetoHelper.obtenerValorPorDefecto(paginacion,
				new ConsultaPaginadaDominio(Paginacion.PAGINA_INICIAL, Paginacion.TAMANO_POR_DEFECTO));
	}

	public GrupoEntidad validar(final GrupoRepositorio repositorio) {
		grupo.validarDatoObligatorio();
		paginacion.validar();
		return grupo.validarExiste(repositorio);
	}

}
