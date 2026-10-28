package co.edu.uco.nota.infraestructura.output.serviciosexternos.azure;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.excepcion.TipoError;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;

public final class ClienteHttpAzure {

	private final HttpClient cliente;

	public ClienteHttpAzure(final HttpClient cliente) {
		this.cliente = cliente;
	}

	public HttpResponse<String> enviar(final HttpRequest solicitud) {
		try {
			return cliente.send(solicitud, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
		} catch (final IOException excepcion) {
			throw NotaException.crear(TipoError.INESPERADO, ClaveMensaje.ERROR_INESPERADO,
					"No se pudo comunicar con " + solicitud.uri().getHost() + ".", excepcion);
		} catch (final InterruptedException excepcion) {
			Thread.currentThread().interrupt();
			throw NotaException.crear(TipoError.INESPERADO, ClaveMensaje.ERROR_INESPERADO,
					"Se interrumpió la llamada a " + solicitud.uri().getHost() + ".", excepcion);
		}
	}

}
