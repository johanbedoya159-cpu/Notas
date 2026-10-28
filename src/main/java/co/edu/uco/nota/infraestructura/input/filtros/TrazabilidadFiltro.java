package co.edu.uco.nota.infraestructura.input.filtros;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import co.edu.uco.nota.crosscutting.helper.UUIDHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TrazabilidadFiltro extends OncePerRequestFilter {

	public static final String CLAVE_MDC = "trazabilidad";
	public static final String ENCABEZADO = "X-Trace-Id";

	@Override
	protected void doFilterInternal(final HttpServletRequest solicitud, final HttpServletResponse respuesta,
			final FilterChain cadena) throws ServletException, IOException {
		final UUID recibida = UUIDHelper.convertir(solicitud.getHeader(ENCABEZADO));
		final String trazabilidad = (UUIDHelper.esPorDefecto(recibida) ? UUIDHelper.generarNuevo() : recibida)
				.toString();
		MDC.put(CLAVE_MDC, trazabilidad);
		respuesta.setHeader(ENCABEZADO, trazabilidad);
		try {
			cadena.doFilter(solicitud, respuesta);
		} finally {
			MDC.remove(CLAVE_MDC);
		}
	}

}
