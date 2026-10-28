package co.edu.uco.nota.servicios.input.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;
import co.edu.uco.nota.dominio.notaestudiante.registrarnotaestudiante.NotaEstudianteDominio;

public record RegistrarNotaEstudianteRequestDTO(UUID docente, UUID estudiante, UUID nota, BigDecimal valor) {

	public RegistrarNotaEstudianteRequestDTO {
		docente = UUIDHelper.obtenerValorPorDefecto(docente);
		estudiante = UUIDHelper.obtenerValorPorDefecto(estudiante);
		nota = UUIDHelper.obtenerValorPorDefecto(nota);
		valor = ObjetoHelper.obtenerValorPorDefecto(valor, NotaEstudianteDominio.VALOR_NO_INFORMADO);
	}

}
