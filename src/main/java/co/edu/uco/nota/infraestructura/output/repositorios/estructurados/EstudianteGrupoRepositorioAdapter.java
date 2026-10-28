package co.edu.uco.nota.infraestructura.output.repositorios.estructurados;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import co.edu.uco.nota.crosscutting.paginacion.Pagina;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades.EstudianteGrupoEntidadJPA;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.especificaciones.EspecificacionesJPA;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.repositorios.EstudianteGrupoJPARepositorio;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.mapper.EntidadMapper;
import co.edu.uco.nota.servicios.output.ports.repositorios.EstudianteGrupoRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.EstudianteGrupoCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteGrupoEntidad;

@Component
public class EstudianteGrupoRepositorioAdapter implements EstudianteGrupoRepositorio {

	private static final Sort ORDEN = Sort.by("estudiante.apellidos", "estudiante.nombres", "id");

	private final EstudianteGrupoJPARepositorio repositorio;
	private final EntidadMapper<EstudianteGrupoEntidad, EstudianteGrupoEntidadJPA> mapper;

	public EstudianteGrupoRepositorioAdapter(final EstudianteGrupoJPARepositorio repositorio,
			final EntidadMapper<EstudianteGrupoEntidad, EstudianteGrupoEntidadJPA> mapper) {
		this.repositorio = repositorio;
		this.mapper = mapper;
	}

	@Override
	public Pagina<EstudianteGrupoEntidad> consultar(final EstudianteGrupoCriterio criterio) {
		final Specification<EstudianteGrupoEntidadJPA> filtro = EspecificacionesJPA
				.<EstudianteGrupoEntidadJPA>igualA(raiz -> raiz.get("estudiante").get("id"), criterio.estudiante())
				.and(EspecificacionesJPA.igualA(raiz -> raiz.get("grupo").get("id"), criterio.grupo()));
		return EspecificacionesJPA.aPagina(
				repositorio.findAll(filtro, EspecificacionesJPA.paginar(criterio.paginacion(), ORDEN)),
				mapper::convertirAEntidad);
	}

}
