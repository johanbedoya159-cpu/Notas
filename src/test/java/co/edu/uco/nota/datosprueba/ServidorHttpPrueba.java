package co.edu.uco.nota.datosprueba;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public final class ServidorHttpPrueba implements AutoCloseable {

	public record Solicitud(String metodo, String rutaYConsulta, Map<String, List<String>> encabezados,
			String cuerpo) {

		public String encabezado(final String nombre) {
			return encabezados.entrySet().stream().filter(e -> e.getKey().equalsIgnoreCase(nombre))
					.map(e -> e.getValue().get(0)).findFirst().orElse("");
		}

	}

	public record Respuesta(int estado, String cuerpo) {
	}

	private final HttpServer servidor;
	private final Map<String, Respuesta> respuestas = new ConcurrentHashMap<>();
	public final List<Solicitud> solicitudes = new CopyOnWriteArrayList<>();

	public ServidorHttpPrueba() throws IOException {
		servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		servidor.createContext("/", this::atender);
		servidor.start();
	}

	public String url() {
		return "http://127.0.0.1:" + servidor.getAddress().getPort();
	}

	public void responder(final String rutaInicial, final int estado, final String cuerpo) {
		respuestas.put(rutaInicial, new Respuesta(estado, cuerpo));
	}

	public long contar(final String rutaInicial) {
		return solicitudes.stream().filter(s -> s.rutaYConsulta().startsWith(rutaInicial)).count();
	}

	private void atender(final HttpExchange intercambio) throws IOException {
		final String ruta = intercambio.getRequestURI().getRawPath()
				+ (intercambio.getRequestURI().getRawQuery() == null ? "" : "?" + intercambio.getRequestURI().getRawQuery());
		final String cuerpo = new String(intercambio.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
		solicitudes.add(new Solicitud(intercambio.getRequestMethod(), ruta, Map.copyOf(intercambio.getRequestHeaders()),
				cuerpo));
		final Respuesta respuesta = respuestas.entrySet().stream().filter(e -> ruta.startsWith(e.getKey()))
				.max((a, b) -> Integer.compare(a.getKey().length(), b.getKey().length())).map(Map.Entry::getValue)
				.orElse(new Respuesta(404, "{}"));
		final byte[] bytes = respuesta.cuerpo().getBytes(StandardCharsets.UTF_8);
		intercambio.sendResponseHeaders(respuesta.estado(), bytes.length == 0 ? -1 : bytes.length);
		try (OutputStream salida = intercambio.getResponseBody()) {
			salida.write(bytes);
		}
	}

	@Override
	public void close() {
		servidor.stop(0);
	}

}
