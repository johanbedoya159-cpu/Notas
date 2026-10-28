package co.edu.uco.nota.infraestructura.output.repositorios.estructurados;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import co.edu.uco.nota.crosscutting.paginacion.Pagina;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades.GrupoEntidadJPA;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.especificaciones.EspecificacionesJPA;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.repositorios.GrupoJPARepositorio;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.mapper.EntidadMapper;
import co.edu.uco.nota.servicios.output.ports.repositorios.GrupoRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.GrupoCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;

@Component
public class GrupoRepositorioAdapter implements GrupoRepositorio {

	private static final Sort ORDEN = Sort.by("codigo", "id");

	private final GrupoJPARepositorio repositorio;
	private final EntidadMapper<GrupoEntidad, GrupoEntidadJPA> mapper;

	public GrupoRepositorioAdapter(final GrupoJPARepositorio repositorio,
			final EntidadMapper<GrupoEntidad, GrupoEntidadJPA> mapper) {
		this.repositorio = repositorio;
		this.mapper = mapper;
	}

	@Override
	public Optional<GrupoEntidad> consultarPorId(final UUID id) {
		return repositorio.findById(id).map(mapper::convertirAEntidad);
	}

	@Override
	public Pagina<GrupoEntidad> consultar(final GrupoCriterio criterio) {
		final Specification<GrupoEntidadJPA> filtro = EspecificacionesJPA
				.<GrupoEntidadJPA>contieneEnAlguno(criterio.texto(), "codigo", "nombre")
				.and(EspecificacionesJPA.igualA(raiz -> raiz.get("docente").get("id"), criterio.docente()));
		return EspecificacionesJPA.aPagina(
				repositorio.findAll(filtro, EspecificacionesJPA.paginar(criterio.paginacion(), ORDEN)),
				mapper::convertirAEntidad);
	}

}
