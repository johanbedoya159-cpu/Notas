package co.edu.uco.nota.infraestructura.output.repositorios.estructurados;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import co.edu.uco.nota.crosscutting.paginacion.Pagina;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades.NotaEntidadJPA;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.especificaciones.EspecificacionesJPA;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.repositorios.NotaJPARepositorio;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.mapper.EntidadMapper;
import co.edu.uco.nota.servicios.output.ports.repositorios.NotaRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.NotaCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEntidad;

@Component
public class NotaRepositorioAdapter implements NotaRepositorio {

	private static final Sort ORDEN = Sort.by("fechaInicioRegistro", "nombre", "id");

	private final NotaJPARepositorio repositorio;
	private final EntidadMapper<NotaEntidad, NotaEntidadJPA> mapper;

	public NotaRepositorioAdapter(final NotaJPARepositorio repositorio,
			final EntidadMapper<NotaEntidad, NotaEntidadJPA> mapper) {
		this.repositorio = repositorio;
		this.mapper = mapper;
	}

	@Override
	public Optional<NotaEntidad> consultarPorId(final UUID id) {
		return repositorio.findById(id).map(mapper::convertirAEntidad);
	}

	@Override
	public Pagina<NotaEntidad> consultar(final NotaCriterio criterio) {
		return EspecificacionesJPA.aPagina(
				repositorio.findAll(
						EspecificacionesJPA.<NotaEntidadJPA>igualA(raiz -> raiz.get("grupo").get("id"), criterio.grupo()),
						EspecificacionesJPA.paginar(criterio.paginacion(), ORDEN)),
				mapper::convertirAEntidad);
	}

}
