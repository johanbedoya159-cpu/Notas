package co.edu.uco.nota.servicios.casosuso.estudiante.consultarmisnotas;

import java.util.List;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteGrupoEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEstudianteEntidad;

public record NotasDeGrupo(EstudianteGrupoEntidad inscripcion, List<NotaEntidad> notas,
		List<NotaEstudianteEntidad> registradas) {

	public NotasDeGrupo {
		inscripcion = ObjetoHelper.obtenerValorPorDefecto(inscripcion, EstudianteGrupoEntidad.vacio());
		notas = List.copyOf(ObjetoHelper.obtenerValorPorDefecto(notas, List.of()));
		registradas = List.copyOf(ObjetoHelper.obtenerValorPorDefecto(registradas, List.of()));
	}

}
