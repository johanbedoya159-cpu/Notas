package co.edu.uco.nota.servicios.casosuso.estudiante.consultarmisnotas;

import static co.edu.uco.nota.datosprueba.DatosPrueba.AHORA;
import static co.edu.uco.nota.datosprueba.DatosPrueba.DOCENTE;
import static co.edu.uco.nota.datosprueba.DatosPrueba.GRUPO;
import static co.edu.uco.nota.datosprueba.DatosPrueba.NOTA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.excepcion.TipoError;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.datosprueba.DatosPrueba;
import co.edu.uco.nota.datosprueba.RepositoriosEnMemoria;
import co.edu.uco.nota.dominio.estudiante.consultarmisnotas.ConsultarMisNotasDominio;
import co.edu.uco.nota.servicios.casosuso.estudiante.consultarmisnotas.impl.ConsultarMisNotasCasoUsoImpl;
import co.edu.uco.nota.servicios.input.adapters.ConsultarMisNotasInteractor;
import co.edu.uco.nota.servicios.input.dto.request.ConsultarMisNotasRequestDTO;
import co.edu.uco.nota.servicios.input.dto.response.MiGrupoResponseDTO;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteGrupoEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEstudianteEntidad;

class ConsultarMisNotasTest {

	private static final UUID SEGUNDA_NOTA = UUID.fromString("019a0000-0000-7000-8000-000000000502");
	private static final UUID OTRO_GRUPO = UUID.fromString("019a0000-0000-7000-8000-000000000302");

	private RepositoriosEnMemoria repositorios;
	private ConsultarMisNotasCasoUsoImpl casoUso;

	@BeforeEach
	void preparar() {
		repositorios = new RepositoriosEnMemoria();
		final GrupoEntidad grupo = DatosPrueba.grupo(DOCENTE, GrupoEntidad.ESTADO_VIGENTE);
		final EstudianteGrupoEntidad inscripcion = DatosPrueba.inscripcion(grupo);
		final NotaEntidad parcial = DatosPrueba.notaAbierta(grupo);
		final NotaEntidad taller = new NotaEntidad(SEGUNDA_NOTA, grupo, "Taller", AHORA, AHORA.plusSeconds(60));
		final GrupoEntidad ajeno = new GrupoEntidad(OTRO_GRUPO, "IS2-02", "Otro grupo", grupo.docente(), "VIGENTE");

		repositorios.estudiantes.put(DatosPrueba.ESTUDIANTE, DatosPrueba.estudiante("ACTIVO"));
		repositorios.grupos.put(GRUPO, grupo);
		repositorios.grupos.put(OTRO_GRUPO, ajeno);
		repositorios.notas.put(NOTA, parcial);
		repositorios.notas.put(SEGUNDA_NOTA, taller);
		repositorios.notas.put(UUID.randomUUID(), new NotaEntidad(UUID.randomUUID(), ajeno, "Ajena", AHORA, AHORA));
		repositorios.inscripciones.add(inscripcion);
		repositorios.notasEstudiantes.add(new NotaEstudianteEntidad(UUID.randomUUID(), inscripcion, parcial, 435, AHORA));

		casoUso = new ConsultarMisNotasCasoUsoImpl(repositorios.estudiante, repositorios.estudianteGrupo,
				repositorios.nota, repositorios.notaEstudiante);
	}

	@Test
	@DisplayName("El estudiante ve solo sus grupos, con las notas registradas y las pendientes")
	void consultaSusNotas() {
		final List<MiGrupoResponseDTO> grupos = new ConsultarMisNotasInteractor(casoUso)
				.ejecutar(new ConsultarMisNotasRequestDTO("  ANA.perez@example.com "));

		assertEquals(1, grupos.size());
		final MiGrupoResponseDTO grupo = grupos.get(0);
		assertEquals("IS2-01", grupo.codigo());
		assertEquals("Carlos Mesa", grupo.nombreDocente());
		assertEquals(2, grupo.notas().size());
		assertEquals(new BigDecimal("4.35"), grupo.notas().get(0).valor());
		assertEquals("Taller", grupo.notas().get(1).nombre());
		assertNull(grupo.notas().get(1).valor());
		assertNull(grupo.notas().get(1).fechaRegistro());
	}

	@Test
	@DisplayName("Sin correo en la sesión no se puede consultar")
	void sinCorreo() {
		final ConsultarMisNotasInteractor interactor = new ConsultarMisNotasInteractor(casoUso);
		final NotaException error = assertThrows(NotaException.class, () -> interactor.ejecutar(null));

		assertEquals(TipoError.DATOS_INVALIDOS, error.getTipo());
		assertEquals(ClaveMensaje.CORREO_OBLIGATORIO, error.getClaveMensajeUsuario());
	}

	@Test
	@DisplayName("Un correo que no es de ningún estudiante responde que no se encontró")
	void correoNoRegistrado() {
		final ConsultarMisNotasDominio consulta = new ConsultarMisNotasDominio("nadie@uco.net.co");
		final NotaException error = assertThrows(NotaException.class, () -> casoUso.ejecutar(consulta));

		assertEquals(TipoError.NO_ENCONTRADO, error.getTipo());
		assertEquals(ClaveMensaje.ESTUDIANTE_SIN_REGISTRO, error.getClaveMensajeUsuario());
		assertEquals("nadie@uco.net.co", error.getParametros()[0]);
	}

	@Test
	@DisplayName("Un estudiante sin grupos recibe una lista vacía y la consulta nula usa valores por defecto")
	void sinGrupos() {
		repositorios.inscripciones.clear();

		assertTrue(casoUso.ejecutar(new ConsultarMisNotasDominio("ana.perez@example.com")).isEmpty());
		assertThrows(NotaException.class, () -> casoUso.ejecutar(null));
		assertTrue(new NotasDeGrupo(null, null, null).notas().isEmpty());
		assertTrue(new MiGrupoResponseDTO(null, null, null, null, null, null).notas().isEmpty());
	}

}
