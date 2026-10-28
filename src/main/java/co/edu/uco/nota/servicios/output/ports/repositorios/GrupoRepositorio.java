package co.edu.uco.nota.servicios.output.ports.repositorios;

import java.util.UUID;

import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.GrupoCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;

public interface GrupoRepositorio extends ConsultarPorIdRepositorio<UUID, GrupoEntidad>,
		ConsultarPorCriterioRepositorio<GrupoEntidad, GrupoCriterio> {

}
