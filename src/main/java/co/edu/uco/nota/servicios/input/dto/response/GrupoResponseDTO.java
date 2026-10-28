package co.edu.uco.nota.servicios.input.dto.response;

import java.util.UUID;

import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;

public record GrupoResponseDTO(UUID id, String codigo, String nombre, UUID docente, String nombreDocente,
		String estado) {

	public static GrupoResponseDTO desde(final GrupoEntidad grupo) {
		return new GrupoResponseDTO(grupo.id(), grupo.codigo(), grupo.nombre(), grupo.docente().id(),
				grupo.docente().nombre(), grupo.estado());
	}

}
