package co.edu.uco.nota.servicios.casosuso.notaestudiante.consultarnotasregistradas.impl;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;
import co.edu.uco.nota.crosscutting.paginacion.Pagina;
import co.edu.uco.nota.dominio.notaestudiante.consultarnotasregistradas.ConsultarNotasRegistradasDominio;
import co.edu.uco.nota.servicios.casosuso.notaestudiante.consultarnotasregistradas.ConsultarNotasRegistradasCasoUso;
import co.edu.uco.nota.servicios.output.ports.repositorios.NotaEstudianteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.NotaRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.NotaEstudianteCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEstudianteEntidad;

public final class ConsultarNotasRegistradasCasoUsoImpl implements ConsultarNotasRegistradasCasoUso {

	private final NotaRepositorio notaRepositorio;
	private final NotaEstudianteRepositorio notaEstudianteRepositorio;

	public ConsultarNotasRegistradasCasoUsoImpl(final NotaRepositorio notaRepositorio,
			final NotaEstudianteRepositorio notaEstudianteRepositorio) {
		this.notaRepositorio = ObjetoHelper.requerirDependencia(notaRepositorio, "el repositorio de notas");
		this.notaEstudianteRepositorio = ObjetoHelper.requerirDependencia(notaEstudianteRepositorio,
				"el repositorio de notas de estudiantes");
	}

	@Override
	public Pagina<NotaEstudianteEntidad> ejecutar(final ConsultarNotasRegistradasDominio datos) {
		final ConsultarNotasRegistradasDominio consulta = ObjetoHelper.obtenerValorPorDefecto(datos,
				new ConsultarNotasRegistradasDominio(null, null));
		final NotaEntidad nota = consulta.validar(notaRepositorio);
		return notaEstudianteRepositorio.consultar(new NotaEstudianteCriterio(UUIDHelper.UUID_POR_DEFECTO,
				nota.id(), consulta.paginacion().aPaginacion()));
	}

}
