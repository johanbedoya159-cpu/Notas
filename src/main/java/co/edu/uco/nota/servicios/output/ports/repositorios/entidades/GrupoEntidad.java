package co.edu.uco.nota.servicios.output.ports.repositorios.entidades;

import java.util.UUID;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.TextoHelper;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;

public record GrupoEntidad(UUID id, String codigo, String nombre, DocenteEntidad docente, String estado) {

	public static final String ESTADO_VIGENTE = "VIGENTE";

	public GrupoEntidad {
		id = UUIDHelper.obtenerValorPorDefecto(id);
		codigo = TextoHelper.aplicarTrim(codigo);
		nombre = TextoHelper.aplicarTrim(nombre);
		docente = ObjetoHelper.obtenerValorPorDefecto(docente, DocenteEntidad.vacio());
		estado = TextoHelper.aplicarTrim(estado);
	}

	public static GrupoEntidad vacio() {
		return new GrupoEntidad(UUIDHelper.UUID_POR_DEFECTO, TextoHelper.VACIO, TextoHelper.VACIO,
				DocenteEntidad.vacio(), TextoHelper.VACIO);
	}

}
