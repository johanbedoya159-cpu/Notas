package co.edu.uco.nota.servicios.output.ports.repositorios.entidades;

import java.util.UUID;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;

public record EstudianteGrupoEntidad(UUID id, EstudianteEntidad estudiante, GrupoEntidad grupo) {

	public EstudianteGrupoEntidad {
		id = UUIDHelper.obtenerValorPorDefecto(id);
		estudiante = ObjetoHelper.obtenerValorPorDefecto(estudiante, EstudianteEntidad.vacio());
		grupo = ObjetoHelper.obtenerValorPorDefecto(grupo, GrupoEntidad.vacio());
	}

	public static EstudianteGrupoEntidad vacio() {
		return new EstudianteGrupoEntidad(UUIDHelper.UUID_POR_DEFECTO, EstudianteEntidad.vacio(), GrupoEntidad.vacio());
	}

}
