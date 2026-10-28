package co.edu.uco.nota.servicios.input.dto.response;

import java.time.Instant;
import java.util.UUID;

import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEntidad;

public record NotaResponseDTO(UUID id, String nombre, Instant fechaInicioRegistro, Instant fechaFinRegistro) {

	public static NotaResponseDTO desde(final NotaEntidad nota) {
		return new NotaResponseDTO(nota.id(), nota.nombre(), nota.fechaInicioRegistro(), nota.fechaFinRegistro());
	}

}
