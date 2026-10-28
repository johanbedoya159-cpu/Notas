package co.edu.uco.nota.servicios.output.ports.repositorios.criterios;

import java.util.UUID;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;
import co.edu.uco.nota.crosscutting.paginacion.Paginacion;

public record EstudianteGrupoCriterio(UUID estudiante, UUID grupo, Paginacion paginacion) {

	public EstudianteGrupoCriterio {
		estudiante = UUIDHelper.obtenerValorPorDefecto(estudiante);
		grupo = UUIDHelper.obtenerValorPorDefecto(grupo);
		paginacion = ObjetoHelper.obtenerValorPorDefecto(paginacion, Paginacion.porDefecto());
	}

}
