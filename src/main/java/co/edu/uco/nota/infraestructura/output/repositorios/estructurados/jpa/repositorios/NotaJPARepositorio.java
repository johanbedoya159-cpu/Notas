package co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.repositorios;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades.NotaEntidadJPA;

@Repository
public interface NotaJPARepositorio extends JpaRepository<NotaEntidadJPA, UUID>,
		JpaSpecificationExecutor<NotaEntidadJPA> {

}
