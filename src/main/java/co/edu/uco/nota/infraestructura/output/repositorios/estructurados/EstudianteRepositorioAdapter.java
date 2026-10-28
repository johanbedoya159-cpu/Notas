package co.edu.uco.nota.infraestructura.output.repositorios.estructurados;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import co.edu.uco.nota.crosscutting.helper.TextoHelper;
import co.edu.uco.nota.crosscutting.paginacion.Pagina;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades.EstudianteEntidadJPA;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.especificaciones.EspecificacionesJPA;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.repositorios.EstudianteJPARepositorio;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.mapper.EntidadMapper;
import co.edu.uco.nota.servicios.output.ports.repositorios.EstudianteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.EstudianteCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteEntidad;

@Component
public class EstudianteRepositorioAdapter implements EstudianteRepositorio {

	private static final Sort ORDEN = Sort.by("apellidos", "nombres", "id");

	private final EstudianteJPARepositorio repositorio;
	private final EntidadMapper<EstudianteEntidad, EstudianteEntidadJPA> mapper;

	public EstudianteRepositorioAdapter(final EstudianteJPARepositorio repositorio,
			final EntidadMapper<EstudianteEntidad, EstudianteEntidadJPA> mapper) {
		this.repositorio = repositorio;
		this.mapper = mapper;
	}

	@Override
	public Optional<EstudianteEntidad> consultarPorId(final UUID id) {
		return repositorio.findById(id).map(mapper::convertirAEntidad);
	}

	@Override
	public Optional<EstudianteEntidad> consultarPorCorreo(final String correo) {
		if (TextoHelper.estaVacio(correo)) {
			return Optional.empty();
		}
		return repositorio.findFirstByCorreoIgnoreCase(TextoHelper.aplicarTrim(correo)).map(mapper::convertirAEntidad);
	}

	@Override
	public Pagina<EstudianteEntidad> consultar(final EstudianteCriterio criterio) {
		return EspecificacionesJPA.aPagina(
				repositorio.findAll(
						EspecificacionesJPA.contieneEnAlguno(criterio.texto(), "identificacion", "nombres", "apellidos"),
						EspecificacionesJPA.paginar(criterio.paginacion(), ORDEN)),
				mapper::convertirAEntidad);
	}

}
