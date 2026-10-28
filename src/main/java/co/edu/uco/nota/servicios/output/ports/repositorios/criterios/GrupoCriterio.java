package co.edu.uco.nota.servicios.output.ports.repositorios.criterios;

import java.util.UUID;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.TextoHelper;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;
import co.edu.uco.nota.crosscutting.paginacion.Paginacion;

public record GrupoCriterio(String texto, UUID docente, Paginacion paginacion) {

	public GrupoCriterio {
		texto = TextoHelper.aplicarTrim(texto);
		docente = UUIDHelper.obtenerValorPorDefecto(docente);
		paginacion = ObjetoHelper.obtenerValorPorDefecto(paginacion, Paginacion.porDefecto());
	}

}
