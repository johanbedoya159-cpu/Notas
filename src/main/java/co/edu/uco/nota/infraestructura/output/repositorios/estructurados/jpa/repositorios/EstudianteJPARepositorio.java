package co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.repositorios;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades.EstudianteEntidadJPA;

@Repository
public interface EstudianteJPARepositorio extends JpaRepository<EstudianteEntidadJPA, UUID>,
		JpaSpecificationExecutor<EstudianteEntidadJPA> {

	Optional<EstudianteEntidadJPA> findFirstByCorreoIgnoreCase(String correo);

}
