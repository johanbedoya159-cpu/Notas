package co.edu.uco.nota.servicios.output.ports.repositorios.entidades;

import java.util.UUID;

import co.edu.uco.nota.crosscutting.helper.TextoHelper;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;

public record DocenteEntidad(UUID id, String identificacion, String nombre, String estado) {

	public static final String ESTADO_ACTIVO = "ACTIVO";

	public DocenteEntidad {
		id = UUIDHelper.obtenerValorPorDefecto(id);
		identificacion = TextoHelper.aplicarTrim(identificacion);
		nombre = TextoHelper.aplicarTrim(nombre);
		estado = TextoHelper.aplicarTrim(estado);
	}

	public static DocenteEntidad vacio() {
		return new DocenteEntidad(UUIDHelper.UUID_POR_DEFECTO, TextoHelper.VACIO, TextoHelper.VACIO, TextoHelper.VACIO);
	}

}
