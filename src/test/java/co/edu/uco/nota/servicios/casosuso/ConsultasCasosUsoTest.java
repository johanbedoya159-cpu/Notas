package co.edu.uco.nota.servicios.casosuso;

import static co.edu.uco.nota.datosprueba.DatosPrueba.DOCENTE;
import static co.edu.uco.nota.datosprueba.DatosPrueba.GRUPO;
import static co.edu.uco.nota.datosprueba.DatosPrueba.NOTA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.crosscutting.paginacion.Pagina;
import co.edu.uco.nota.datosprueba.DatosPrueba;
import co.edu.uco.nota.datosprueba.RepositoriosEnMemoria;
import co.edu.uco.nota.dominio.comun.ConsultaPaginadaDominio;
import co.edu.uco.nota.dominio.comun.GrupoExistenteDominio;
import co.edu.uco.nota.dominio.comun.TextoBusquedaDominio;
import co.edu.uco.nota.dominio.estudiante.consultarestudiantes.ConsultarEstudiantesDominio;
import co.edu.uco.nota.dominio.grupo.consultarestudiantesgrupo.ConsultarEstudiantesGrupoDominio;
import co.edu.uco.nota.dominio.grupo.consultargrupos.ConsultarGruposDominio;
import co.edu.uco.nota.dominio.nota.consultarnotasgrupo.ConsultarNotasGrupoDominio;
import co.edu.uco.nota.dominio.notaestudiante.consultarnotasregistradas.ConsultarNotasRegistradasDominio;
import co.edu.uco.nota.dominio.notaestudiante.registrarnotaestudiante.NotaDominio;
import co.edu.uco.nota.servicios.casosuso.estudiante.consultarestudiantes.impl.ConsultarEstudiantesCasoUsoImpl;
import co.edu.uco.nota.servicios.casosuso.grupo.consultarestudiantesgrupo.impl.ConsultarEstudiantesGrupoCasoUsoImpl;
import co.edu.uco.nota.servicios.casosuso.grupo.consultargrupos.impl.ConsultarGruposCasoUsoImpl;
import co.edu.uco.nota.servicios.casosuso.nota.consultarnotasgrupo.impl.ConsultarNotasGrupoCasoUsoImpl;
import co.edu.uco.nota.servicios.casosuso.notaestudiante.consultarnotasregistradas.impl.ConsultarNotasRegistradasCasoUsoImpl;
import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.EstudianteCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.GrupoCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEstudianteEntidad;

class ConsultasCasosUsoTest {

	private static final UUID INEXISTENTE = UUID.fromString("019a0000-0000-7000-8000-000000000999");

	private RepositoriosEnMemoria memoria;

	@BeforeEach
	void preparar() {
		memoria = new RepositoriosEnMemoria();
		final GrupoEntidad grupo = DatosPrueba.grupo(DOCENTE, "VIGENTE");
		memoria.grupos.put(GRUPO, grupo);
		memoria.estudiantes.put(DatosPrueba.ESTUDIANTE, DatosPrueba.estudiante("ACTIVO"));
		memoria.estudiantes.put(INEXISTENTE, new EstudianteEntidad(INEXISTENTE, "1002", "Bruno", "Díaz", "ACTIVO", ""));
		memoria.inscripciones.add(DatosPrueba.inscripcion(grupo));
		memoria.notas.put(NOTA, DatosPrueba.notaAbierta(grupo));
		memoria.notasEstudiantes.add(new NotaEstudianteEntidad(UUID.randomUUID(), DatosPrueba.inscripcion(grupo),
				memoria.notas.get(NOTA), 420, Instant.EPOCH));
	}

	private static ConsultaPaginadaDominio pagina(final int pagina, final int tamano) {
		return new ConsultaPaginadaDominio(pagina, tamano);
	}

	@Test
	@DisplayName("Busca estudiantes por texto y pasa la paginación al repositorio")
	void consultaEstudiantes() {
		final Pagina<EstudianteEntidad> resultado = new ConsultarEstudiantesCasoUsoImpl(memoria.estudiante)
				.ejecutar(new ConsultarEstudiantesDominio(new TextoBusquedaDominio(" ana "), pagina(0, 10)));

		assertEquals(1, resultado.totalElementos());
		assertEquals("Ana Pérez", resultado.contenido().get(0).nombreCompleto());
		final EstudianteCriterio criterio = (EstudianteCriterio) memoria.criteriosRecibidos.get(0);
		assertEquals("ana", criterio.texto());
		assertEquals(10, criterio.paginacion().tamano());
	}

	@Test
	@DisplayName("Usa la paginación por defecto cuando no llega la consulta")
	void consultaEstudiantesPorDefecto() {
		final Pagina<EstudianteEntidad> resultado = new ConsultarEstudiantesCasoUsoImpl(memoria.estudiante)
				.ejecutar(null);

		assertEquals(2, resultado.totalElementos());
		assertEquals(20, resultado.tamano());
	}

	@Test
	@DisplayName("Rechaza una página negativa, un tamaño fuera de rango y un texto muy largo")
	void rechazaConsultaInvalida() {
		final ConsultarEstudiantesCasoUsoImpl casoUso = new ConsultarEstudiantesCasoUsoImpl(memoria.estudiante);
		final TextoBusquedaDominio vacio = new TextoBusquedaDominio("");

		assertEquals(ClaveMensaje.PAGINA_INVALIDA, assertThrows(NotaException.class,
				() -> casoUso.ejecutar(new ConsultarEstudiantesDominio(vacio, pagina(-1, 10)))).getClaveMensajeUsuario());
		assertEquals(ClaveMensaje.TAMANO_DE_PAGINA_INVALIDO, assertThrows(NotaException.class,
				() -> casoUso.ejecutar(new ConsultarEstudiantesDominio(vacio, pagina(0, 51)))).getClaveMensajeUsuario());
		assertEquals(ClaveMensaje.TAMANO_DE_PAGINA_INVALIDO, assertThrows(NotaException.class,
				() -> casoUso.ejecutar(new ConsultarEstudiantesDominio(vacio, pagina(0, 0)))).getClaveMensajeUsuario());
		final TextoBusquedaDominio largo = new TextoBusquedaDominio("a".repeat(101));
		assertEquals(ClaveMensaje.TEXTO_DE_BUSQUEDA_MUY_LARGO, assertThrows(NotaException.class,
				() -> casoUso.ejecutar(new ConsultarEstudiantesDominio(largo, pagina(0, 10)))).getClaveMensajeUsuario());
	}

	@Test
	@DisplayName("Busca grupos filtrando por docente")
	void consultaGrupos() {
		final ConsultarGruposCasoUsoImpl casoUso = new ConsultarGruposCasoUsoImpl(memoria.grupo);

		assertEquals(1, casoUso.ejecutar(new ConsultarGruposDominio(null, DOCENTE, pagina(0, 5))).totalElementos());
		assertEquals(0, casoUso.ejecutar(new ConsultarGruposDominio(null, INEXISTENTE, pagina(0, 5))).totalElementos());
		assertEquals(DOCENTE, ((GrupoCriterio) memoria.criteriosRecibidos.get(0)).docente());
		assertEquals(1, casoUso.ejecutar(null).totalElementos());
	}

	@Test
	@DisplayName("Lista los estudiantes inscritos en un grupo que existe")
	void consultaEstudiantesDelGrupo() {
		final ConsultarEstudiantesGrupoCasoUsoImpl casoUso = new ConsultarEstudiantesGrupoCasoUsoImpl(memoria.grupo,
				memoria.estudianteGrupo);

		assertEquals(1, casoUso.ejecutar(new ConsultarEstudiantesGrupoDominio(new GrupoExistenteDominio(GRUPO),
				pagina(0, 10))).totalElementos());
		assertEquals(ClaveMensaje.GRUPO_NO_EXISTE,
				assertThrows(NotaException.class, () -> casoUso.ejecutar(
						new ConsultarEstudiantesGrupoDominio(new GrupoExistenteDominio(INEXISTENTE), pagina(0, 10))))
						.getClaveMensajeUsuario());
		assertEquals(ClaveMensaje.GRUPO_OBLIGATORIO,
				assertThrows(NotaException.class, () -> casoUso.ejecutar(null)).getClaveMensajeUsuario());
	}

	@Test
	@DisplayName("Lista las notas de un grupo que existe")
	void consultaNotasDelGrupo() {
		final ConsultarNotasGrupoCasoUsoImpl casoUso = new ConsultarNotasGrupoCasoUsoImpl(memoria.grupo,
				memoria.nota);

		assertEquals("Parcial 1", casoUso
				.ejecutar(new ConsultarNotasGrupoDominio(new GrupoExistenteDominio(GRUPO), pagina(0, 10))).contenido()
				.get(0).nombre());
		assertEquals(ClaveMensaje.GRUPO_OBLIGATORIO,
				assertThrows(NotaException.class, () -> casoUso.ejecutar(null)).getClaveMensajeUsuario());
	}

	@Test
	@DisplayName("Lista las notas registradas de una nota que existe")
	void consultaNotasRegistradas() {
		final ConsultarNotasRegistradasCasoUsoImpl casoUso = new ConsultarNotasRegistradasCasoUsoImpl(memoria.nota,
				memoria.notaEstudiante);

		assertEquals(420, casoUso
				.ejecutar(new ConsultarNotasRegistradasDominio(new NotaDominio(NOTA), pagina(0, 10))).contenido()
				.get(0).valor());
		assertEquals(ClaveMensaje.NOTA_NO_EXISTE,
				assertThrows(NotaException.class, () -> casoUso.ejecutar(
						new ConsultarNotasRegistradasDominio(new NotaDominio(INEXISTENTE), pagina(0, 10))))
						.getClaveMensajeUsuario());
		assertEquals(ClaveMensaje.NOTA_OBLIGATORIA,
				assertThrows(NotaException.class, () -> casoUso.ejecutar(null)).getClaveMensajeUsuario());
	}

	@Test
	@DisplayName("Los casos de uso de consulta exigen sus repositorios")
	void exigenRepositorios() {
		assertThrows(NotaException.class, () -> new ConsultarEstudiantesCasoUsoImpl(null));
		assertThrows(NotaException.class, () -> new ConsultarGruposCasoUsoImpl(null));
		assertThrows(NotaException.class, () -> new ConsultarEstudiantesGrupoCasoUsoImpl(memoria.grupo, null));
		assertThrows(NotaException.class, () -> new ConsultarNotasGrupoCasoUsoImpl(null, memoria.nota));
		assertThrows(NotaException.class, () -> new ConsultarNotasRegistradasCasoUsoImpl(memoria.nota, null));
	}

}
