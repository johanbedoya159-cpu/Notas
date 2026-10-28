package co.edu.uco.nota.servicios.input.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;
import co.edu.uco.nota.crosscutting.paginacion.Pagina;
import co.edu.uco.nota.datosprueba.DatosPrueba;
import co.edu.uco.nota.dominio.estudiante.consultarestudiantes.ConsultarEstudiantesDominio;
import co.edu.uco.nota.dominio.grupo.consultargrupos.ConsultarGruposDominio;
import co.edu.uco.nota.dominio.notaestudiante.registrarnotaestudiante.NotaEstudianteDominio;
import co.edu.uco.nota.dominio.notaestudiante.registrarnotaestudiante.RegistrarNotaEstudianteDominio;
import co.edu.uco.nota.servicios.input.dto.request.ConsultarEstudiantesGrupoRequestDTO;
import co.edu.uco.nota.servicios.input.dto.request.ConsultarEstudiantesRequestDTO;
import co.edu.uco.nota.servicios.input.dto.request.ConsultarGruposRequestDTO;
import co.edu.uco.nota.servicios.input.dto.request.ConsultarNotasGrupoRequestDTO;
import co.edu.uco.nota.servicios.input.dto.request.ConsultarNotasRegistradasRequestDTO;
import co.edu.uco.nota.servicios.input.dto.request.RegistrarNotaEstudianteRequestDTO;
import co.edu.uco.nota.servicios.input.dto.response.EstudianteResponseDTO;
import co.edu.uco.nota.servicios.input.dto.response.GrupoResponseDTO;
import co.edu.uco.nota.servicios.input.dto.response.NotaEstudianteResponseDTO;
import co.edu.uco.nota.servicios.input.dto.response.NotaResponseDTO;
import co.edu.uco.nota.servicios.input.dto.response.PaginaResponseDTO;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEstudianteEntidad;

class InteractoresTest {

	private static final GrupoEntidad GRUPO = DatosPrueba.grupo(DatosPrueba.DOCENTE, "VIGENTE");

	@Test
	@DisplayName("Convierte la solicitud de registro en el dominio de la transacción")
	void registrarConvierteSolicitud() {
		final List<RegistrarNotaEstudianteDominio> recibidos = new ArrayList<>();
		final RegistrarNotaEstudianteInteractor interactor = new RegistrarNotaEstudianteInteractor(recibidos::add);

		interactor.ejecutar(new RegistrarNotaEstudianteRequestDTO(DatosPrueba.DOCENTE, DatosPrueba.ESTUDIANTE,
				DatosPrueba.NOTA, new BigDecimal("4.5")));
		interactor.ejecutar(null);

		assertEquals(DatosPrueba.DOCENTE, recibidos.get(0).docente().id());
		assertEquals(DatosPrueba.ESTUDIANTE, recibidos.get(0).estudiante().id());
		assertEquals(DatosPrueba.NOTA, recibidos.get(0).nota().id());
		assertEquals(new BigDecimal("4.5"), recibidos.get(0).notaEstudiante().valor());
		assertTrue(UUIDHelper.esPorDefecto(recibidos.get(1).docente().id()));
		assertEquals(NotaEstudianteDominio.VALOR_NO_INFORMADO, recibidos.get(1).notaEstudiante().valor());
	}

	@Test
	@DisplayName("Convierte la búsqueda de estudiantes y su respuesta")
	void consultarEstudiantes() {
		final List<ConsultarEstudiantesDominio> recibidos = new ArrayList<>();
		final ConsultarEstudiantesInteractor interactor = new ConsultarEstudiantesInteractor(consulta -> {
			recibidos.add(consulta);
			return new Pagina<>(List.of(DatosPrueba.estudiante("ACTIVO")), 1, 5, 6);
		});

		final PaginaResponseDTO<EstudianteResponseDTO> respuesta = interactor
				.ejecutar(new ConsultarEstudiantesRequestDTO(" ana ", 1, 5));

		assertEquals("ana", recibidos.get(0).texto().valor());
		assertEquals(5, recibidos.get(0).paginacion().tamano());
		assertEquals("Ana Pérez", respuesta.contenido().get(0).nombreCompleto());
		assertEquals(2, respuesta.totalPaginas());
		interactor.ejecutar(null);
		assertEquals("", recibidos.get(1).texto().valor());
		assertEquals(20, recibidos.get(1).paginacion().tamano());
	}

	@Test
	@DisplayName("Convierte la búsqueda de grupos con el nombre del docente")
	void consultarGrupos() {
		final List<ConsultarGruposDominio> recibidos = new ArrayList<>();
		final ConsultarGruposInteractor interactor = new ConsultarGruposInteractor(consulta -> {
			recibidos.add(consulta);
			return new Pagina<>(List.of(GRUPO), 0, 20, 1);
		});

		final GrupoResponseDTO grupo = interactor
				.ejecutar(new ConsultarGruposRequestDTO("is2", DatosPrueba.DOCENTE, null, null)).contenido().get(0);

		assertEquals(DatosPrueba.DOCENTE, recibidos.get(0).docente());
		assertEquals(20, recibidos.get(0).paginacion().tamano());
		assertEquals("Carlos Mesa", grupo.nombreDocente());
		assertEquals("IS2-01", grupo.codigo());
		assertEquals(1, interactor.ejecutar(null).totalElementos());
	}

	@Test
	@DisplayName("Convierte las consultas por grupo y por nota")
	void consultarPorGrupoYNota() {
		final UUID grupo = DatosPrueba.GRUPO;
		final PaginaResponseDTO<EstudianteResponseDTO> estudiantes = new ConsultarEstudiantesGrupoInteractor(
				consulta -> new Pagina<>(List.of(DatosPrueba.inscripcion(GRUPO)), 0, 20, 1))
				.ejecutar(new ConsultarEstudiantesGrupoRequestDTO(grupo, null, null));
		final PaginaResponseDTO<NotaResponseDTO> notas = new ConsultarNotasGrupoInteractor(
				consulta -> new Pagina<>(List.of(DatosPrueba.notaAbierta(GRUPO)), 0, 20, 1))
				.ejecutar(new ConsultarNotasGrupoRequestDTO(grupo, null, null));
		final PaginaResponseDTO<NotaEstudianteResponseDTO> registradas = new ConsultarNotasRegistradasInteractor(
				consulta -> new Pagina<>(List.of(new NotaEstudianteEntidad(UUID.randomUUID(),
						DatosPrueba.inscripcion(GRUPO), DatosPrueba.notaAbierta(GRUPO), 435, Instant.EPOCH)), 0, 20, 1))
				.ejecutar(new ConsultarNotasRegistradasRequestDTO(DatosPrueba.NOTA, null, null));

		assertEquals("1001", estudiantes.contenido().get(0).identificacion());
		assertEquals("Parcial 1", notas.contenido().get(0).nombre());
		assertEquals(new BigDecimal("4.35"), registradas.contenido().get(0).valor());
		assertEquals("Ana Pérez", registradas.contenido().get(0).nombreEstudiante());
		assertEquals(20, new ConsultarEstudiantesGrupoInteractor(c -> Pagina.vacia(c.paginacion().aPaginacion()))
				.ejecutar(null).tamano());
		assertEquals(0, new ConsultarNotasGrupoInteractor(c -> Pagina.vacia(c.paginacion().aPaginacion()))
				.ejecutar(null).totalElementos());
		assertEquals(0, new ConsultarNotasRegistradasInteractor(c -> Pagina.vacia(c.paginacion().aPaginacion()))
				.ejecutar(null).totalElementos());
	}

	@Test
	@DisplayName("Los interactores exigen su caso de uso")
	void exigenCasoUso() {
		assertThrows(NotaException.class, () -> new RegistrarNotaEstudianteInteractor(null));
		assertThrows(NotaException.class, () -> new ConsultarEstudiantesInteractor(null));
		assertThrows(NotaException.class, () -> new ConsultarGruposInteractor(null));
		assertThrows(NotaException.class, () -> new ConsultarEstudiantesGrupoInteractor(null));
		assertThrows(NotaException.class, () -> new ConsultarNotasGrupoInteractor(null));
		assertThrows(NotaException.class, () -> new ConsultarNotasRegistradasInteractor(null));
	}

}
