package co.edu.uco.nota.infraestructura.input.autorizacion;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.TextoHelper;

public record UsuarioSesion(String correo, String nombre, Rol rol) {

	public UsuarioSesion {
		correo = TextoHelper.aplicarTrim(correo);
		nombre = TextoHelper.aplicarTrim(nombre);
		rol = ObjetoHelper.obtenerValorPorDefecto(rol, Rol.SIN_ROL);
	}

	public static UsuarioSesion anonimo() {
		return new UsuarioSesion(TextoHelper.VACIO, TextoHelper.VACIO, Rol.SIN_ROL);
	}

	public UsuarioSesion comoProfesor() {
		return new UsuarioSesion(correo, nombre, Rol.PROFESOR);
	}

}
