package co.edu.uco.nota.servicios.casosuso.estudiante.consultarmisnotas.impl;

import java.util.List;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;
import co.edu.uco.nota.crosscutting.paginacion.Paginacion;
import co.edu.uco.nota.dominio.estudiante.consultarmisnotas.ConsultarMisNotasDominio;
import co.edu.uco.nota.servicios.casosuso.estudiante.consultarmisnotas.ConsultarMisNotasCasoUso;
import co.edu.uco.nota.servicios.casosuso.estudiante.consultarmisnotas.NotasDeGrupo;
import co.edu.uco.nota.servicios.output.ports.repositorios.EstudianteGrupoRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.EstudianteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.NotaEstudianteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.NotaRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.EstudianteGrupoCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.NotaCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.NotaEstudianteCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteGrupoEntidad;

public final class ConsultarMisNotasCasoUsoImpl implements ConsultarMisNotasCasoUso {

	private static final Paginacion TODAS = new Paginacion(Paginacion.PAGINA_INICIAL, Paginacion.TAMANO_MAXIMO);

	private final EstudianteRepositorio estudianteRepositorio;
	private final EstudianteGrupoRepositorio estudianteGrupoRepositorio;
	private final NotaRepositorio notaRepositorio;
	private final NotaEstudianteRepositorio notaEstudianteRepositorio;

	public ConsultarMisNotasCasoUsoImpl(final EstudianteRepositorio estudianteRepositorio,
			final EstudianteGrupoRepositorio estudianteGrupoRepositorio, final NotaRepositorio notaRepositorio,
			final NotaEstudianteRepositorio notaEstudianteRepositorio) {
		this.estudianteRepositorio = ObjetoHelper.requerirDependencia(estudianteRepositorio,
				"el repositorio de estudiantes");
		this.estudianteGrupoRepositorio = ObjetoHelper.requerirDependencia(estudianteGrupoRepositorio,
				"el repositorio de inscripciones");
		this.notaRepositorio = ObjetoHelper.requerirDependencia(notaRepositorio, "el repositorio de notas");
		this.notaEstudianteRepositorio = ObjetoHelper.requerirDependencia(notaEstudianteRepositorio,
				"el repositorio de notas de estudiantes");
	}

	@Override
	public List<NotasDeGrupo> ejecutar(final ConsultarMisNotasDominio datos) {
		final ConsultarMisNotasDominio consulta = ObjetoHelper.obtenerValorPorDefecto(datos,
				new ConsultarMisNotasDominio(null));
		final EstudianteEntidad estudiante = consulta.validar(estudianteRepositorio);
		return estudianteGrupoRepositorio
				.consultar(new EstudianteGrupoCriterio(estudiante.id(), UUIDHelper.UUID_POR_DEFECTO, TODAS))
				.contenido().stream().map(this::notasDe).toList();
	}

	private NotasDeGrupo notasDe(final EstudianteGrupoEntidad inscripcion) {
		return new NotasDeGrupo(inscripcion,
				notaRepositorio.consultar(new NotaCriterio(inscripcion.grupo().id(), TODAS)).contenido(),
				notaEstudianteRepositorio.consultar(
						new NotaEstudianteCriterio(inscripcion.id(), UUIDHelper.UUID_POR_DEFECTO, TODAS)).contenido());
	}

}
