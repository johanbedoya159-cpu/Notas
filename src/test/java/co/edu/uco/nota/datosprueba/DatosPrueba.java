package co.edu.uco.nota.datosprueba;

import java.time.Instant;
import java.util.UUID;

import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.DocenteEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteGrupoEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEntidad;

public final class DatosPrueba {

	public static final UUID DOCENTE = UUID.fromString("019a0000-0000-7000-8000-000000000101");
	public static final UUID OTRO_DOCENTE = UUID.fromString("019a0000-0000-7000-8000-000000000103");
	public static final UUID ESTUDIANTE = UUID.fromString("019a0000-0000-7000-8000-000000000201");
	public static final UUID GRUPO = UUID.fromString("019a0000-0000-7000-8000-000000000301");
	public static final UUID INSCRIPCION = UUID.fromString("019a0000-0000-7000-8000-000000000401");
	public static final UUID NOTA = UUID.fromString("019a0000-0000-7000-8000-000000000501");
	public static final Instant AHORA = Instant.parse("2026-10-28T15:00:00Z");

	private DatosPrueba() {
	}

	public static DocenteEntidad docente(final UUID id, final String estado) {
		return new DocenteEntidad(id, "10101", "Carlos Mesa", estado);
	}

	public static EstudianteEntidad estudiante(final String estado) {
		return new EstudianteEntidad(ESTUDIANTE, "1001", "Ana", "Pérez", estado, "ana.perez@example.com");
	}

	public static GrupoEntidad grupo(final UUID docente, final String estado) {
		return new GrupoEntidad(GRUPO, "IS2-01", "Ingeniería de Software 2", docente(docente, "ACTIVO"), estado);
	}

	public static EstudianteGrupoEntidad inscripcion(final GrupoEntidad grupo) {
		return new EstudianteGrupoEntidad(INSCRIPCION, estudiante("ACTIVO"), grupo);
	}

	public static NotaEntidad nota(final GrupoEntidad grupo, final Instant inicio, final Instant fin) {
		return new NotaEntidad(NOTA, grupo, "Parcial 1", inicio, fin);
	}

	public static NotaEntidad notaAbierta(final GrupoEntidad grupo) {
		return nota(grupo, AHORA.minusSeconds(86_400), AHORA.plusSeconds(86_400));
	}

}
