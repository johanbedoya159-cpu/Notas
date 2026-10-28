package co.edu.uco.nota.infraestructura.input.autorizacion;

import org.springframework.web.servlet.HandlerInterceptor;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.excepcion.TipoError;
import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Decide qué puede hacer cada rol. Con la autorización apagada (desarrollo local y pruebas) todos entran como
 * profesor, igual que antes de agregar el inicio de sesión.
 */
public final class AutorizacionInterceptor implements HandlerInterceptor {

	public static final String ATRIBUTO_USUARIO = "nota.usuario";

	private final boolean habilitada;
	private final LectorPrincipalAzure lector;

	public AutorizacionInterceptor(final boolean habilitada, final LectorPrincipalAzure lector) {
		this.habilitada = habilitada;
		this.lector = ObjetoHelper.requerirDependencia(lector, "el lector del usuario de la sesión");
	}

	@Override
	public boolean preHandle(final HttpServletRequest solicitud, final HttpServletResponse respuesta,
			final Object manejador) {
		final UsuarioSesion leido = lector.leer(solicitud);
		final UsuarioSesion usuario = habilitada ? leido : leido.comoProfesor();
		solicitud.setAttribute(ATRIBUTO_USUARIO, usuario);
		final String ruta = solicitud.getRequestURI().substring(solicitud.getContextPath().length());
		if (usuario.rol().puedeAcceder(solicitud.getMethod(), ruta)) {
			return true;
		}
		throw NotaException.crear(TipoError.ACCESO_DENEGADO, ClaveMensaje.ACCESO_DENEGADO, "El usuario '"
				+ usuario.correo() + "' con rol " + usuario.rol() + " intentó " + solicitud.getMethod() + " " + ruta + ".");
	}

	public boolean estaHabilitada() {
		return habilitada;
	}

}
