package co.edu.uco.nota.servicios.input.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import co.edu.uco.nota.dominio.notaestudiante.registrarnotaestudiante.NotaEstudianteDominio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEstudianteEntidad;

public record MiNotaResponseDTO(UUID id, String nombre, BigDecimal valor, Instant fechaRegistro) {

	public static MiNotaResponseDTO pendiente(final NotaEntidad nota) {
		return new MiNotaResponseDTO(nota.id(), nota.nombre(), null, null);
	}

	public static MiNotaResponseDTO registrada(final NotaEntidad nota, final NotaEstudianteEntidad registro) {
		return new MiNotaResponseDTO(nota.id(), nota.nombre(),
				BigDecimal.valueOf(registro.valor(), NotaEstudianteDominio.DECIMALES_PERMITIDOS),
				registro.fechaRegistro());
	}

}
