package co.edu.uco.nota.dominio.grupo.consultarestudiantesgrupo;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.paginacion.Paginacion;
import co.edu.uco.nota.dominio.comun.ConsultaPaginadaDominio;
import co.edu.uco.nota.dominio.comun.GrupoExistenteDominio;
import co.edu.uco.nota.servicios.output.ports.repositorios.GrupoRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;

public record ConsultarEstudiantesGrupoDominio(GrupoExistenteDominio grupo, ConsultaPaginadaDominio paginacion) {

	public ConsultarEstudiantesGrupoDominio {
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
