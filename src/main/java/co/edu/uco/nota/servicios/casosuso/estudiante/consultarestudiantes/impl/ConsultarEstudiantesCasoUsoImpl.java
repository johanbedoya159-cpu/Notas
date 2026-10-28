package co.edu.uco.nota.servicios.casosuso.estudiante.consultarestudiantes.impl;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.paginacion.Pagina;
import co.edu.uco.nota.dominio.estudiante.consultarestudiantes.ConsultarEstudiantesDominio;
import co.edu.uco.nota.servicios.casosuso.estudiante.consultarestudiantes.ConsultarEstudiantesCasoUso;
import co.edu.uco.nota.servicios.output.ports.repositorios.EstudianteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.EstudianteCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteEntidad;

public final class ConsultarEstudiantesCasoUsoImpl implements ConsultarEstudiantesCasoUso {

	private final EstudianteRepositorio repositorio;

	public ConsultarEstudiantesCasoUsoImpl(final EstudianteRepositorio repositorio) {
		this.repositorio = ObjetoHelper.requerirDependencia(repositorio, "el repositorio de estudiantes");
	}

	@Override
	public Pagina<EstudianteEntidad> ejecutar(final ConsultarEstudiantesDominio datos) {
		final ConsultarEstudiantesDominio consulta = ObjetoHelper.obtenerValorPorDefecto(datos,
				new ConsultarEstudiantesDominio(null, null));
		consulta.validar();
		return repositorio.consultar(
				new EstudianteCriterio(consulta.texto().valor(), consulta.paginacion().aPaginacion()));
	}

}
