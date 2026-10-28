package co.edu.uco.nota.servicios.casosuso.grupo.consultargrupos.impl;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.paginacion.Pagina;
import co.edu.uco.nota.dominio.grupo.consultargrupos.ConsultarGruposDominio;
import co.edu.uco.nota.servicios.casosuso.grupo.consultargrupos.ConsultarGruposCasoUso;
import co.edu.uco.nota.servicios.output.ports.repositorios.GrupoRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.GrupoCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;

public final class ConsultarGruposCasoUsoImpl implements ConsultarGruposCasoUso {

	private final GrupoRepositorio repositorio;

	public ConsultarGruposCasoUsoImpl(final GrupoRepositorio repositorio) {
		this.repositorio = ObjetoHelper.requerirDependencia(repositorio, "el repositorio de grupos");
	}

	@Override
	public Pagina<GrupoEntidad> ejecutar(final ConsultarGruposDominio datos) {
		final ConsultarGruposDominio consulta = ObjetoHelper.obtenerValorPorDefecto(datos,
				new ConsultarGruposDominio(null, null, null));
		consulta.validar();
		return repositorio.consultar(new GrupoCriterio(consulta.texto().valor(), consulta.docente(),
				consulta.paginacion().aPaginacion()));
	}

}
