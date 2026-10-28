package co.edu.uco.nota.servicios.output.ports.repositorios;

import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.NotaEstudianteCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEstudianteEntidad;

public interface NotaEstudianteRepositorio extends CrearRepositorio<NotaEstudianteEntidad>,
		ConsultarPorCriterioRepositorio<NotaEstudianteEntidad, NotaEstudianteCriterio> {

}
