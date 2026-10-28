package co.edu.uco.nota.dominio.notaestudiante.registrarnotaestudiante;

import java.math.BigDecimal;
import java.util.UUID;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;

public record RegistrarNotaEstudianteDominio(DocenteDominio docente, EstudianteDominio estudiante, NotaDominio nota,
		NotaEstudianteDominio notaEstudiante) {

	public RegistrarNotaEstudianteDominio {
		docente = ObjetoHelper.obtenerValorPorDefecto(docente, new DocenteDominio(null));
		estudiante = ObjetoHelper.obtenerValorPorDefecto(estudiante, new EstudianteDominio(null));
		nota = ObjetoHelper.obtenerValorPorDefecto(nota, new NotaDominio(null));
		notaEstudiante = ObjetoHelper.obtenerValorPorDefecto(notaEstudiante, new NotaEstudianteDominio(null));
	}

	public static RegistrarNotaEstudianteDominio crear(final UUID docente, final UUID estudiante, final UUID nota,
			final BigDecimal valor) {
		return new RegistrarNotaEstudianteDominio(new DocenteDominio(docente), new EstudianteDominio(estudiante),
				new NotaDominio(nota), new NotaEstudianteDominio(valor));
	}

	public void validarDatos() {
		docente.validarDatoObligatorio();
		estudiante.validarDatoObligatorio();
		nota.validarDatoObligatorio();
		notaEstudiante.validarValor();
	}

}
