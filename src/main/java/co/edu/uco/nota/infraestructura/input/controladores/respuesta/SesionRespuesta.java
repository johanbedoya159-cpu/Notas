package co.edu.uco.nota.infraestructura.input.controladores.respuesta;

import co.edu.uco.nota.infraestructura.input.autorizacion.UsuarioSesion;

public record SesionRespuesta(String correo, String nombre, String rol) {

	public static SesionRespuesta desde(final UsuarioSesion usuario) {
		return new SesionRespuesta(usuario.correo(), usuario.nombre(), usuario.rol().name());
	}

}
