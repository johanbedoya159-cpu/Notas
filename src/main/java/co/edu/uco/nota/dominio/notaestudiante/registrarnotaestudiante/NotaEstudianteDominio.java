package co.edu.uco.nota.dominio.notaestudiante.registrarnotaestudiante;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.excepcion.TipoError;
import co.edu.uco.nota.crosscutting.helper.NumeroHelper;
import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.crosscutting.paginacion.Paginacion;
import co.edu.uco.nota.crosscutting.parametros.ClaveParametro;
import co.edu.uco.nota.servicios.output.ports.repositorios.NotaEstudianteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.NotaEstudianteCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteGrupoEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEstudianteEntidad;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.catalogos.CatalogoParametrosServicio;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.notificaciones.NotificacionCorreo;

public record NotaEstudianteDominio(BigDecimal valor) {

	public static final int DECIMALES_PERMITIDOS = 2;
	public static final BigDecimal VALOR_NO_INFORMADO = BigDecimal.ONE.negate();

	public NotaEstudianteDominio {
		valor = ObjetoHelper.obtenerValorPorDefecto(valor, VALOR_NO_INFORMADO);
	}

	public void validarValor(final CatalogoParametrosServicio parametros) {
		final BigDecimal minimo = leerParametro(parametros, ClaveParametro.NOTA_VALOR_MINIMO);
		final BigDecimal maximo = leerParametro(parametros, ClaveParametro.NOTA_VALOR_MAXIMO);

		if (valor.compareTo(minimo) < 0 || valor.compareTo(maximo) > 0) {
			throw NotaException.crear(TipoError.DATOS_INVALIDOS, ClaveMensaje.VALOR_FUERA_DE_RANGO,
					"El valor " + valor.toPlainString() + " está fuera del rango permitido de " + minimo.toPlainString()
							+ " a " + maximo.toPlainString() + ".",
					minimo, maximo);
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

	public Optional<NotificacionCorreo> crearNotificacion(final EstudianteEntidad estudiante, final NotaEntidad nota,
			final GrupoEntidad grupo, final CatalogoParametrosServicio parametros) {
		if (!estudiante.tieneCorreo()) {
			return Optional.empty();
		}

		final BigDecimal aprobatorio = leerParametro(parametros, ClaveParametro.NOTA_VALOR_APROBATORIO);
		final ClaveMensaje cuerpo = valor.compareTo(aprobatorio) >= 0 ? ClaveMensaje.NOTIFICACION_NOTA_REGISTRADA_APROBADA
				: ClaveMensaje.NOTIFICACION_NOTA_REGISTRADA_NO_APROBADA;

		return Optional.of(new NotificacionCorreo(estudiante.correo(), estudiante.nombreCompleto(),
				ClaveMensaje.NOTIFICACION_NOTA_REGISTRADA_ASUNTO, cuerpo,
				List.of(estudiante.nombres(), nota.nombre(), grupo.nombre(),
						NumeroHelper.formatearConDecimales(valor, DECIMALES_PERMITIDOS),
						NumeroHelper.formatearConDecimales(aprobatorio, 1))));
	}

	private static BigDecimal leerParametro(final CatalogoParametrosServicio parametros, final ClaveParametro clave) {
		final BigDecimal porDefecto = new BigDecimal(clave.getValorPorDefecto());
		return NumeroHelper.convertirADecimal(parametros.obtenerValor(clave), porDefecto);
	}

}
