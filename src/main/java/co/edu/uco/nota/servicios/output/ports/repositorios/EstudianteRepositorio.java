package co.edu.uco.nota.servicios.output.ports.repositorios;

import java.util.UUID;

import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.EstudianteCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteEntidad;

public interface EstudianteRepositorio extends ConsultarPorIdRepositorio<UUID, EstudianteEntidad>,
		ConsultarPorCriterioRepositorio<EstudianteEntidad, EstudianteCriterio> {

}
