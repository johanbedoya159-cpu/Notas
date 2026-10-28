package co.edu.uco.nota.infraestructura.input.autorizacion;

import java.util.Set;

public enum Rol {

	PROFESOR,
	ESTUDIANTE,
	SIN_ROL;

	public static final String RUTA_SESION = "/api/v1/sesion";
	public static final String RUTA_MIS_NOTAS = "/api/v1/mis-notas";

	private static final String METODO_CONSULTA = "GET";
	private static final Set<String> RUTAS_ESTUDIANTE = Set.of(RUTA_SESION, RUTA_MIS_NOTAS);

	public boolean puedeAcceder(final String metodo, final String ruta) {
		return switch (this) {
		case PROFESOR -> true;
		case ESTUDIANTE -> METODO_CONSULTA.equalsIgnoreCase(metodo) && RUTAS_ESTUDIANTE.contains(ruta);
		case SIN_ROL -> METODO_CONSULTA.equalsIgnoreCase(metodo) && RUTA_SESION.equals(ruta);
		};
	}

}
