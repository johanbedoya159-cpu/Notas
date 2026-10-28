package co.edu.uco.nota.servicios.output.ports.repositorios;

import co.edu.uco.nota.crosscutting.paginacion.Pagina;

public interface ConsultarPorCriterioRepositorio<E, C> {

	Pagina<E> consultar(C criterio);

}
