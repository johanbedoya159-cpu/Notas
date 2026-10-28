package co.edu.uco.nota.servicios.output.ports.repositorios.criterios;

import java.util.UUID;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;
import co.edu.uco.nota.crosscutting.paginacion.Paginacion;

public record NotaEstudianteCriterio(UUID estudianteGrupo, UUID nota, Paginacion paginacion) {

	public NotaEstudianteCriterio {
		estudianteGrupo = UUIDHelper.obtenerValorPorDefecto(estudianteGrupo);
		nota = UUIDHelper.obtenerValorPorDefecto(nota);
		paginacion = ObjetoHelper.obtenerValorPorDefecto(paginacion, Paginacion.porDefecto());
	}

}
