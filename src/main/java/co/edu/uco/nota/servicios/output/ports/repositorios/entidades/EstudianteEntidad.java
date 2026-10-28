package co.edu.uco.nota.servicios.output.ports.repositorios.entidades;

import java.util.UUID;

import co.edu.uco.nota.crosscutting.helper.TextoHelper;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;

public record EstudianteEntidad(UUID id, String identificacion, String nombres, String apellidos, String estado) {

	public static final String ESTADO_ACTIVO = "ACTIVO";

	public EstudianteEntidad {
		id = UUIDHelper.obtenerValorPorDefecto(id);
		identificacion = TextoHelper.aplicarTrim(identificacion);
		nombres = TextoHelper.aplicarTrim(nombres);
		apellidos = TextoHelper.aplicarTrim(apellidos);
		estado = TextoHelper.aplicarTrim(estado);
	}

	public static EstudianteEntidad vacio() {
		return new EstudianteEntidad(UUIDHelper.UUID_POR_DEFECTO, TextoHelper.VACIO, TextoHelper.VACIO,
				TextoHelper.VACIO, TextoHelper.VACIO);
	}

	public String nombreCompleto() {
		return (nombres + " " + apellidos).trim();
	}

}
