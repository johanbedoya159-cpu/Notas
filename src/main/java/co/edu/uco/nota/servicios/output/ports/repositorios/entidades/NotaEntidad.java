package co.edu.uco.nota.servicios.output.ports.repositorios.entidades;

import java.time.Instant;
import java.util.UUID;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.TextoHelper;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;

public record NotaEntidad(UUID id, GrupoEntidad grupo, String nombre, Instant fechaInicioRegistro,
		Instant fechaFinRegistro) {

	public NotaEntidad {
		id = UUIDHelper.obtenerValorPorDefecto(id);
		grupo = ObjetoHelper.obtenerValorPorDefecto(grupo, GrupoEntidad.vacio());
		nombre = TextoHelper.aplicarTrim(nombre);
		fechaInicioRegistro = ObjetoHelper.obtenerValorPorDefecto(fechaInicioRegistro, Instant.EPOCH);
		fechaFinRegistro = ObjetoHelper.obtenerValorPorDefecto(fechaFinRegistro, Instant.EPOCH);
	}

	public static NotaEntidad vacio() {
		return new NotaEntidad(UUIDHelper.UUID_POR_DEFECTO, GrupoEntidad.vacio(), TextoHelper.VACIO, Instant.EPOCH,
				Instant.EPOCH);
	}

}
