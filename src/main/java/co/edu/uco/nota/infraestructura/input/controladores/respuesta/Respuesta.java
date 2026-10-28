package co.edu.uco.nota.infraestructura.input.controladores.respuesta;

import java.util.List;

import org.slf4j.MDC;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.TextoHelper;
import co.edu.uco.nota.infraestructura.input.filtros.TrazabilidadFiltro;

public record Respuesta<T>(boolean exito, List<String> mensajes, T datos, String trazabilidad) {

	public Respuesta {
		mensajes = List.copyOf(ObjetoHelper.obtenerValorPorDefecto(mensajes, List.of()));
		trazabilidad = TextoHelper.aplicarTrim(trazabilidad);
	}

	public static <T> Respuesta<T> exitosa(final T datos) {
		return new Respuesta<>(true, List.of(), datos, trazabilidadActual());
	}

	public static Respuesta<List<Object>> exitosa(final String mensaje) {
		return new Respuesta<>(true, List.of(mensaje), List.of(), trazabilidadActual());
	}

	public static Respuesta<List<Object>> fallida(final String mensaje) {
		return new Respuesta<>(false, List.of(mensaje), List.of(), trazabilidadActual());
	}

	private static String trazabilidadActual() {
		return MDC.get(TrazabilidadFiltro.CLAVE_MDC);
	}

}
