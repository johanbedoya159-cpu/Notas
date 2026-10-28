package co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.repositorios;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades.NotaEstudianteEntidadJPA;

@Repository
public interface NotaEstudianteJPARepositorio extends JpaRepository<NotaEstudianteEntidadJPA, UUID>,
		JpaSpecificationExecutor<NotaEstudianteEntidadJPA> {

}
