package co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.especificaciones;

import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import co.edu.uco.nota.crosscutting.helper.TextoHelper;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;
import co.edu.uco.nota.crosscutting.paginacion.Pagina;
import co.edu.uco.nota.crosscutting.paginacion.Paginacion;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public final class EspecificacionesJPA {

	private static final char ESCAPE = '\\';

	private EspecificacionesJPA() {
	}

	public static <J> Specification<J> todos() {
		return (raiz, consulta, constructor) -> constructor.conjunction();
	}

	public static <J> Specification<J> igualA(final Function<Path<J>, Path<UUID>> campo, final UUID valor) {
		if (UUIDHelper.esPorDefecto(valor)) {
			return todos();
		}
		return (raiz, consulta, constructor) -> constructor.equal(campo.apply(raiz), valor);
	}

	public static <J> Specification<J> contieneEnAlguno(final String texto, final String... campos) {
		if (TextoHelper.estaVacio(texto)) {
			return todos();
		}
		final String patron = "%" + escapar(TextoHelper.aplicarTrim(texto).toLowerCase(Locale.ROOT)) + "%";
		return (raiz, consulta, constructor) -> constructor.or(Arrays.stream(campos)
				.map(campo -> constructor.like(constructor.lower(raiz.get(campo)), patron, ESCAPE))
				.toArray(Predicate[]::new));
	}

	public static Pageable paginar(final Paginacion paginacion, final Sort orden) {
		return PageRequest.of(paginacion.pagina(), paginacion.tamano(), orden);
	}

	public static <J, E> Pagina<E> aPagina(final Page<J> pagina, final Function<J, E> conversor) {
		return new Pagina<>(pagina.getContent().stream().map(conversor).toList(), pagina.getNumber(),
				pagina.getSize(), pagina.getTotalElements());
	}

	private static String escapar(final String texto) {
		return texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
	}

}
