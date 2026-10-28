package co.edu.uco.nota.infraestructura.output.repositorios.estructurados;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades.DocenteEntidadJPA;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.repositorios.DocenteJPARepositorio;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.mapper.EntidadMapper;
import co.edu.uco.nota.servicios.output.ports.repositorios.DocenteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.DocenteEntidad;

@Component
public class DocenteRepositorioAdapter implements DocenteRepositorio {

	private final DocenteJPARepositorio repositorio;
	private final EntidadMapper<DocenteEntidad, DocenteEntidadJPA> mapper;

	public DocenteRepositorioAdapter(final DocenteJPARepositorio repositorio,
			final EntidadMapper<DocenteEntidad, DocenteEntidadJPA> mapper) {
		this.repositorio = repositorio;
		this.mapper = mapper;
	}

	@Override
	public Optional<DocenteEntidad> consultarPorId(final UUID id) {
		return repositorio.findById(id).map(mapper::convertirAEntidad);
	}

}
