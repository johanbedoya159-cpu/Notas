package co.edu.uco.nota.crosscutting.paginacion;

import java.util.List;
import java.util.function.Function;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;

public record Pagina<T>(List<T> contenido, int pagina, int tamano, long totalElementos) {

	public Pagina {
		contenido = List.copyOf(ObjetoHelper.obtenerValorPorDefecto(contenido, List.of()));
	}

	public static <T> Pagina<T> vacia(final Paginacion paginacion) {
		return new Pagina<>(List.of(), paginacion.pagina(), paginacion.tamano(), 0);
	}

	public int totalPaginas() {
		return tamano <= 0 ? 0 : (int) ((totalElementos + tamano - 1) / tamano);
	}

	public boolean tieneElementos() {
		return totalElementos > 0;
	}

	public <R> Pagina<R> convertir(final Function<T, R> conversor) {
		return new Pagina<>(contenido.stream().map(conversor).toList(), pagina, tamano, totalElementos);
	}

}
