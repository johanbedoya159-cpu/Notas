package co.edu.uco.nota.infraestructura.output.repositorios.estructurados.mapper.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.uco.nota.crosscutting.helper.UUIDHelper;
import co.edu.uco.nota.datosprueba.DatosPrueba;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades.NotaEstudianteEntidadJPA;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEstudianteEntidad;

class MappersTest {

	private final DocenteEntidadMapper docente = new DocenteEntidadMapper();
	private final EstudianteEntidadMapper estudiante = new EstudianteEntidadMapper();
	private final GrupoEntidadMapper grupo = new GrupoEntidadMapper(docente);
	private final EstudianteGrupoEntidadMapper inscripcion = new EstudianteGrupoEntidadMapper(estudiante, grupo);
	private final NotaEntidadMapper nota = new NotaEntidadMapper(grupo);
	private final NotaEstudianteEntidadMapper notaEstudiante = new NotaEstudianteEntidadMapper(inscripcion, nota);

	@Test
	@DisplayName("Convierte la nota de un estudiante a JPA y de vuelta sin perder datos")
	void idaYVuelta() {
		final GrupoEntidad grupoEntidad = DatosPrueba.grupo(DatosPrueba.DOCENTE, "VIGENTE");
		final NotaEstudianteEntidad original = new NotaEstudianteEntidad(UUID.randomUUID(),
				DatosPrueba.inscripcion(grupoEntidad), DatosPrueba.notaAbierta(grupoEntidad), 435,
				Instant.parse("2026-10-28T15:00:00Z"));

		final NotaEstudianteEntidadJPA jpa = notaEstudiante.convertirAEntidadJPA(original);

		assertEquals(original, notaEstudiante.convertirAEntidad(jpa));
		assertEquals("Carlos Mesa", jpa.getEstudianteGrupo().getGrupo().getDocente().getNombre());
		assertEquals("Pérez", jpa.getEstudianteGrupo().getEstudiante().getApellidos());
		assertEquals("Parcial 1", jpa.getNota().getNombre());
	}

	@Test
	@DisplayName("Convierte valores ausentes en entidades vacías, nunca en nulos")
	void sinNulos() {
		assertTrue(UUIDHelper.esPorDefecto(docente.convertirAEntidad(null).id()));
		assertTrue(UUIDHelper.esPorDefecto(estudiante.convertirAEntidad(null).id()));
		assertTrue(UUIDHelper.esPorDefecto(grupo.convertirAEntidad(null).docente().id()));
		assertTrue(UUIDHelper.esPorDefecto(inscripcion.convertirAEntidad(null).grupo().id()));
		assertEquals(Instant.EPOCH, nota.convertirAEntidad(null).fechaFinRegistro());
		assertEquals(0, notaEstudiante.convertirAEntidad(null).valor());
		assertEquals(UUIDHelper.UUID_POR_DEFECTO, notaEstudiante.convertirAEntidadJPA(null).getId());
		assertEquals("", docente.convertirAEntidadJPA(null).getNombre());
		assertEquals("", estudiante.convertirAEntidadJPA(null).getNombres());
		assertEquals("", grupo.convertirAEntidadJPA(null).getCodigo());
		assertEquals(UUIDHelper.UUID_POR_DEFECTO, inscripcion.convertirAEntidadJPA(null).getId());
		assertEquals("", nota.convertirAEntidadJPA(null).getNombre());
	}

}
