package co.edu.uco.nota.crosscutting.excepcion;

import java.util.Arrays;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.TextoHelper;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;

public final class NotaException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final TipoError tipo;
	private final ClaveMensaje claveMensajeUsuario;
	private final transient Object[] parametros;

	private NotaException(final TipoError tipo, final ClaveMensaje claveMensajeUsuario, final String mensajeTecnico,
			final Throwable causaRaiz, final Object... parametros) {
		super(TextoHelper.aplicarTrim(mensajeTecnico), causaRaiz);
		this.tipo = ObjetoHelper.obtenerValorPorDefecto(tipo, TipoError.INESPERADO);
		this.claveMensajeUsuario = ObjetoHelper.obtenerValorPorDefecto(claveMensajeUsuario, ClaveMensaje.ERROR_INESPERADO);
		this.parametros = parametros == null ? new Object[0] : Arrays.copyOf(parametros, parametros.length);
	}

	public static NotaException crear(final TipoError tipo, final ClaveMensaje claveMensajeUsuario,
			final String mensajeTecnico, final Object... parametros) {
		return new NotaException(tipo, claveMensajeUsuario, mensajeTecnico, null, parametros);
	}

	public static NotaException crear(final TipoError tipo, final ClaveMensaje claveMensajeUsuario,
			final String mensajeTecnico, final Throwable causaRaiz) {
		return new NotaException(tipo, claveMensajeUsuario, mensajeTecnico, causaRaiz);
	}

	public TipoError getTipo() {
		return tipo;
	}

	public ClaveMensaje getClaveMensajeUsuario() {
		return claveMensajeUsuario;
	}

	public String getMensajeTecnico() {
		return getMessage();
	}

	public Object[] getParametros() {
		return Arrays.copyOf(parametros, parametros.length);
	}

}
