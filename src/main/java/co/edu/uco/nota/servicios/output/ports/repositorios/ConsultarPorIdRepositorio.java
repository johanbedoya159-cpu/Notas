package co.edu.uco.nota.servicios.output.ports.repositorios;

import java.util.Optional;

public interface ConsultarPorIdRepositorio<I, E> {

	Optional<E> consultarPorId(I id);

}
