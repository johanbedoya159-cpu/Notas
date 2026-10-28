package co.edu.uco.nota.servicios.input.dto.response;

import java.util.UUID;

import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteEntidad;

public record EstudianteResponseDTO(UUID id, String identificacion, String nombreCompleto, String estado) {

	public static EstudianteResponseDTO desde(final EstudianteEntidad estudiante) {
		return new EstudianteResponseDTO(estudiante.id(), estudiante.identificacion(), estudiante.nombreCompleto(),
				estudiante.estado());
	}

}
