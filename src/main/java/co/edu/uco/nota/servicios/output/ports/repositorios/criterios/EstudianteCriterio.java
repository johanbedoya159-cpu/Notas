package co.edu.uco.nota.servicios.output.ports.repositorios.criterios;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.TextoHelper;
import co.edu.uco.nota.crosscutting.paginacion.Paginacion;

public record EstudianteCriterio(String texto, Paginacion paginacion) {

	public EstudianteCriterio {
		texto = TextoHelper.aplicarTrim(texto);
		paginacion = ObjetoHelper.obtenerValorPorDefecto(paginacion, Paginacion.porDefecto());
	}

}
