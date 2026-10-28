package co.edu.uco.nota.servicios.casosuso.grupo.consultarestudiantesgrupo.impl;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;
import co.edu.uco.nota.crosscutting.paginacion.Pagina;
import co.edu.uco.nota.dominio.grupo.consultarestudiantesgrupo.ConsultarEstudiantesGrupoDominio;
import co.edu.uco.nota.servicios.casosuso.grupo.consultarestudiantesgrupo.ConsultarEstudiantesGrupoCasoUso;
import co.edu.uco.nota.servicios.output.ports.repositorios.EstudianteGrupoRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.GrupoRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.EstudianteGrupoCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteGrupoEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;

public final class ConsultarEstudiantesGrupoCasoUsoImpl implements ConsultarEstudiantesGrupoCasoUso {

	private final GrupoRepositorio grupoRepositorio;
	private final EstudianteGrupoRepositorio estudianteGrupoRepositorio;

	public ConsultarEstudiantesGrupoCasoUsoImpl(final GrupoRepositorio grupoRepositorio,
			final EstudianteGrupoRepositorio estudianteGrupoRepositorio) {
		this.grupoRepositorio = ObjetoHelper.requerirDependencia(grupoRepositorio, "el repositorio de grupos");
		this.estudianteGrupoRepositorio = ObjetoHelper.requerirDependencia(estudianteGrupoRepositorio,
				"el repositorio de inscripciones");
	}

	@Override
	public Pagina<EstudianteGrupoEntidad> ejecutar(final ConsultarEstudiantesGrupoDominio datos) {
		final ConsultarEstudiantesGrupoDominio consulta = ObjetoHelper.obtenerValorPorDefecto(datos,
				new ConsultarEstudiantesGrupoDominio(null, null));
		final GrupoEntidad grupo = consulta.validar(grupoRepositorio);
		return estudianteGrupoRepositorio.consultar(new EstudianteGrupoCriterio(UUIDHelper.UUID_POR_DEFECTO,
				grupo.id(), consulta.paginacion().aPaginacion()));
	}

}
