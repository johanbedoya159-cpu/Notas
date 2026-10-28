package co.edu.uco.nota.datosprueba;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import co.edu.uco.nota.crosscutting.helper.UUIDHelper;
import co.edu.uco.nota.crosscutting.paginacion.Pagina;
import co.edu.uco.nota.crosscutting.paginacion.Paginacion;
import co.edu.uco.nota.servicios.output.ports.repositorios.DocenteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.EstudianteGrupoRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.EstudianteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.GrupoRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.NotaEstudianteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.NotaRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.EstudianteCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.EstudianteGrupoCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.GrupoCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.NotaCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.NotaEstudianteCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.DocenteEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteGrupoEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEstudianteEntidad;

public final class RepositoriosEnMemoria {

	public final Map<UUID, DocenteEntidad> docentes = new LinkedHashMap<>();
	public final Map<UUID, EstudianteEntidad> estudiantes = new LinkedHashMap<>();
	public final Map<UUID, GrupoEntidad> grupos = new LinkedHashMap<>();
	public final Map<UUID, NotaEntidad> notas = new LinkedHashMap<>();
	public final List<EstudianteGrupoEntidad> inscripciones = new ArrayList<>();
	public final List<NotaEstudianteEntidad> notasEstudiantes = new ArrayList<>();
	public final List<Object> criteriosRecibidos = new ArrayList<>();

	public final DocenteRepositorio docente = id -> Optional.ofNullable(docentes.get(id));

	public final EstudianteRepositorio estudiante = new EstudianteRepositorio() {

		@Override
		public Optional<EstudianteEntidad> consultarPorId(final UUID id) {
			return Optional.ofNullable(estudiantes.get(id));
		}

		@Override
		public Pagina<EstudianteEntidad> consultar(final EstudianteCriterio criterio) {
			criteriosRecibidos.add(criterio);
			return paginar(new ArrayList<>(estudiantes.values()),
					item -> item.nombreCompleto().toLowerCase().contains(criterio.texto().toLowerCase()),
					criterio.paginacion());
		}

	};

	public final GrupoRepositorio grupo = new GrupoRepositorio() {

		@Override
		public Optional<GrupoEntidad> consultarPorId(final UUID id) {
			return Optional.ofNullable(grupos.get(id));
		}

		@Override
		public Pagina<GrupoEntidad> consultar(final GrupoCriterio criterio) {
			criteriosRecibidos.add(criterio);
			return paginar(new ArrayList<>(grupos.values()),
					item -> UUIDHelper.esPorDefecto(criterio.docente()) || item.docente().id().equals(criterio.docente()),
					criterio.paginacion());
		}

	};

	public final NotaRepositorio nota = new NotaRepositorio() {

		@Override
		public Optional<NotaEntidad> consultarPorId(final UUID id) {
			return Optional.ofNullable(notas.get(id));
		}

		@Override
		public Pagina<NotaEntidad> consultar(final NotaCriterio criterio) {
			criteriosRecibidos.add(criterio);
			return paginar(new ArrayList<>(notas.values()), item -> item.grupo().id().equals(criterio.grupo()),
					criterio.paginacion());
		}

	};

	public final EstudianteGrupoRepositorio estudianteGrupo = criterio -> {
		criteriosRecibidos.add(criterio);
		return paginar(inscripciones, item -> coincide(criterio, item), criterio.paginacion());
	};

	public final NotaEstudianteRepositorio notaEstudiante = new NotaEstudianteRepositorio() {

		@Override
		public void crear(final NotaEstudianteEntidad entidad) {
			notasEstudiantes.add(entidad);
		}

		@Override
		public Pagina<NotaEstudianteEntidad> consultar(final NotaEstudianteCriterio criterio) {
			criteriosRecibidos.add(criterio);
			return paginar(notasEstudiantes,
					item -> (UUIDHelper.esPorDefecto(criterio.estudianteGrupo())
							|| item.estudianteGrupo().id().equals(criterio.estudianteGrupo()))
							&& item.nota().id().equals(criterio.nota()),
					criterio.paginacion());
		}

	};

	private static boolean coincide(final EstudianteGrupoCriterio criterio, final EstudianteGrupoEntidad item) {
		return (UUIDHelper.esPorDefecto(criterio.estudiante()) || item.estudiante().id().equals(criterio.estudiante()))
				&& (UUIDHelper.esPorDefecto(criterio.grupo()) || item.grupo().id().equals(criterio.grupo()));
	}

	private static <T> Pagina<T> paginar(final List<T> elementos, final Predicate<T> filtro,
			final Paginacion paginacion) {
		final List<T> filtrados = elementos.stream().filter(filtro).toList();
		final List<T> pagina = filtrados.stream().skip((long) paginacion.pagina() * paginacion.tamano())
				.limit(paginacion.tamano()).toList();
		return new Pagina<>(pagina, paginacion.pagina(), paginacion.tamano(), filtrados.size());
	}

}
