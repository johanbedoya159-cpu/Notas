package co.edu.uco.nota.servicios.input.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import co.edu.uco.nota.dominio.notaestudiante.registrarnotaestudiante.NotaEstudianteDominio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEstudianteEntidad;

public record NotaEstudianteResponseDTO(UUID id, UUID estudiante, String identificacionEstudiante,
		String nombreEstudiante, BigDecimal valor, Instant fechaRegistro) {

	public static NotaEstudianteResponseDTO desde(final NotaEstudianteEntidad notaEstudiante) {
		final EstudianteEntidad estudiante = notaEstudiante.estudianteGrupo().estudiante();
		return new NotaEstudianteResponseDTO(notaEstudiante.id(), estudiante.id(), estudiante.identificacion(),
				estudiante.nombreCompleto(),
				BigDecimal.valueOf(notaEstudiante.valor(), NotaEstudianteDominio.DECIMALES_PERMITIDOS),
				notaEstudiante.fechaRegistro());
	}

}
