package co.edu.uco.nota.servicios.output.ports.repositorios;

import java.util.UUID;

import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.NotaCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEntidad;

public interface NotaRepositorio extends ConsultarPorIdRepositorio<UUID, NotaEntidad>,
		ConsultarPorCriterioRepositorio<NotaEntidad, NotaCriterio> {

}
