package co.edu.uco.nota.infraestructura.output.repositorios.estructurados;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.excepcion.TipoError;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.crosscutting.paginacion.Pagina;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades.NotaEstudianteEntidadJPA;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.especificaciones.EspecificacionesJPA;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.repositorios.NotaEstudianteJPARepositorio;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.mapper.EntidadMapper;
import co.edu.uco.nota.servicios.output.ports.repositorios.NotaEstudianteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.NotaEstudianteCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEstudianteEntidad;

@Component
public class NotaEstudianteRepositorioAdapter implements NotaEstudianteRepositorio {

	private static final Sort ORDEN = Sort.by("estudianteGrupo.estudiante.apellidos",
			"estudianteGrupo.estudiante.nombres", "id");

	private final NotaEstudianteJPARepositorio repositorio;
	private final EntidadMapper<NotaEstudianteEntidad, NotaEstudianteEntidadJPA> mapper;

	public NotaEstudianteRepositorioAdapter(final NotaEstudianteJPARepositorio repositorio,
			final EntidadMapper<NotaEstudianteEntidad, NotaEstudianteEntidadJPA> mapper) {
		this.repositorio = repositorio;
		this.mapper = mapper;
	}

	@Override
	public void crear(final NotaEstudianteEntidad entidad) {
		try {
			repositorio.saveAndFlush(mapper.convertirAEntidadJPA(entidad));
		} catch (final DataIntegrityViolationException excepcion) {
			throw NotaException.crear(TipoError.CONFLICTO, ClaveMensaje.NOTA_YA_REGISTRADA,
					"La base rechazó la nota " + entidad.nota().id() + " del estudiante del grupo "
							+ entidad.estudianteGrupo().id() + " porque ya existe.",
					excepcion);
		}
	}

	@Override
	public Pagina<NotaEstudianteEntidad> consultar(final NotaEstudianteCriterio criterio) {
		final Specification<NotaEstudianteEntidadJPA> filtro = EspecificacionesJPA
				.<NotaEstudianteEntidadJPA>igualA(raiz -> raiz.get("estudianteGrupo").get("id"),
						criterio.estudianteGrupo())
				.and(EspecificacionesJPA.igualA(raiz -> raiz.get("nota").get("id"), criterio.nota()));
		return EspecificacionesJPA.aPagina(
				repositorio.findAll(filtro, EspecificacionesJPA.paginar(criterio.paginacion(), ORDEN)),
				mapper::convertirAEntidad);
	}

}
