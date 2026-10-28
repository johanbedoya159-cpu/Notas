package co.edu.uco.nota.servicios.output.ports.repositorios.entidades;

import java.time.Instant;
import java.util.UUID;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;

public record NotaEstudianteEntidad(UUID id, EstudianteGrupoEntidad estudianteGrupo, NotaEntidad nota, int valor,
		Instant fechaRegistro) {

	public NotaEstudianteEntidad {
		id = UUIDHelper.obtenerValorPorDefecto(id);
		estudianteGrupo = ObjetoHelper.obtenerValorPorDefecto(estudianteGrupo, EstudianteGrupoEntidad.vacio());
		nota = ObjetoHelper.obtenerValorPorDefecto(nota, NotaEntidad.vacio());
		fechaRegistro = ObjetoHelper.obtenerValorPorDefecto(fechaRegistro, Instant.EPOCH);
	}

}
