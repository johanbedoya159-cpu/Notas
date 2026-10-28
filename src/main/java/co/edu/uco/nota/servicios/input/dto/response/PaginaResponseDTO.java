package co.edu.uco.nota.servicios.input.dto.response;

import java.util.List;
import java.util.function.Function;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.paginacion.Pagina;

public record PaginaResponseDTO<T>(List<T> contenido, int pagina, int tamano, long totalElementos,
		int totalPaginas) {

	public PaginaResponseDTO {
		contenido = List.copyOf(ObjetoHelper.obtenerValorPorDefecto(contenido, List.of()));
	}

	public static <E, T> PaginaResponseDTO<T> desde(final Pagina<E> pagina, final Function<E, T> conversor) {
		return new PaginaResponseDTO<>(pagina.contenido().stream().map(conversor).toList(), pagina.pagina(),
				pagina.tamano(), pagina.totalElementos(), pagina.totalPaginas());
	}

}
