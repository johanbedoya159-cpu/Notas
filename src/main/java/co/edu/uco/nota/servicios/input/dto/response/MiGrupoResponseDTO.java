package co.edu.uco.nota.servicios.input.dto.response;

import java.util.List;
import java.util.UUID;

public record MiGrupoResponseDTO(UUID id, String codigo, String nombre, String nombreDocente, String estado,
		List<MiNotaResponseDTO> notas) {

	public MiGrupoResponseDTO {
		notas = notas == null ? List.of() : List.copyOf(notas);
	}

}
