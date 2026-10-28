package co.edu.uco.nota.dominio.notaestudiante.registrarnotaestudiante;

import java.math.BigDecimal;
import java.time.Instant;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.excepcion.TipoError;
import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.crosscutting.paginacion.Paginacion;
import co.edu.uco.nota.servicios.output.ports.repositorios.NotaEstudianteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.NotaEstudianteCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteGrupoEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEstudianteEntidad;

public record NotaEstudianteDominio(BigDecimal valor) {

	public static final BigDecimal VALOR_MINIMO = BigDecimal.ZERO;
	public static final BigDecimal VALOR_MAXIMO = new BigDecimal("5");
	public static final int DECIMALES_PERMITIDOS = 2;
	public static final BigDecimal VALOR_NO_INFORMADO = BigDecimal.ONE.negate();

	public NotaEstudianteDominio {
		valor = ObjetoHelper.obtenerValorPorDefecto(valor, VALOR_NO_INFORMADO);
	}

	public void validarValor() {
		if (valor.compareTo(VALOR_MINIMO) < 0 || valor.compareTo(VALOR_MAXIMO) > 0) {
			throw NotaException.crear(TipoError.DATOS_INVALIDOS, ClaveMensaje.VALOR_FUERA_DE_RANGO,
					"El valor " + valor.toPlainString() + " está fuera del rango permitido.");
		}

		if (valor.stripTrailingZeros().scale() > DECIMALES_PERMITIDOS) {
			throw NotaException.crear(TipoError.DATOS_INVALIDOS, ClaveMensaje.VALOR_CON_MAS_DE_DOS_DECIMALES,
					"El valor " + valor.toPlainString() + " tiene más de dos decimales.");
		}
	}

	public int obtenerValorEscalado() {
		return valor.movePointRight(DECIMALES_PERMITIDOS).intValueExact();
	}

	public void validarEstudianteSinNotaRegistrada(final NotaEstudianteRepositorio repositorio,
			final EstudianteGrupoEntidad estudianteGrupo, final NotaEntidad nota) {
		final boolean yaRegistrada = repositorio
				.consultar(new NotaEstudianteCriterio(estudianteGrupo.id(), nota.id(), Paginacion.unico()))
				.tieneElementos();

		if (yaRegistrada) {
			throw NotaException.crear(TipoError.CONFLICTO, ClaveMensaje.NOTA_YA_REGISTRADA,
					"El estudiante del grupo " + estudianteGrupo.id() + " ya tiene la nota " + nota.id() + ".");
		}
	}

	public NotaEstudianteEntidad crearRegistro(final EstudianteGrupoEntidad estudianteGrupo, final NotaEntidad nota,
			final Instant ahora) {
		return new NotaEstudianteEntidad(UUIDHelper.generarNuevo(), estudianteGrupo, nota, obtenerValorEscalado(),
				ahora);
	}

}
