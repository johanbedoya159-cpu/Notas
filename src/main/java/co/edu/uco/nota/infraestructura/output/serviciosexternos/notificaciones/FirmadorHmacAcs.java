package co.edu.uco.nota.infraestructura.output.serviciosexternos.notificaciones;

import java.net.URI;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.excepcion.TipoError;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;

public final class FirmadorHmacAcs {

	public static final String ENCABEZADOS_FIRMADOS = "x-ms-date;host;x-ms-content-sha256";

	private static final String ALGORITMO_HMAC = "HmacSHA256";

	private FirmadorHmacAcs() {
	}

	public static HttpRequest crearSolicitud(final ConexionAcs conexion, final String rutaYConsulta,
			final String cuerpo, final Instant ahora, final Duration tiempoMaximo) {
		final URI uri = URI.create(conexion.endpoint() + rutaYConsulta);
		final String fecha = DateTimeFormatter.RFC_1123_DATE_TIME.format(ahora.atOffset(ZoneOffset.UTC));
		final String resumen = resumirContenido(cuerpo);
		final String firma = firmar(conexion.claveAcceso(),
				cadenaAFirmar("POST", rutaYConsulta, fecha, uri.getRawAuthority(), resumen));

		return HttpRequest.newBuilder(uri).timeout(tiempoMaximo).header("Content-Type", "application/json")
				.header("x-ms-date", fecha).header("x-ms-content-sha256", resumen)
				.header("Authorization", "HMAC-SHA256 SignedHeaders=" + ENCABEZADOS_FIRMADOS + "&Signature=" + firma)
				.POST(HttpRequest.BodyPublishers.ofString(cuerpo, StandardCharsets.UTF_8)).build();
	}

	public static String cadenaAFirmar(final String metodo, final String rutaYConsulta, final String fecha,
			final String host, final String resumen) {
		return metodo + "\n" + rutaYConsulta + "\n" + fecha + ";" + host + ";" + resumen;
	}

	public static String resumirContenido(final String cuerpo) {
		try {
			return Base64.getEncoder().encodeToString(
					MessageDigest.getInstance("SHA-256").digest(cuerpo.getBytes(StandardCharsets.UTF_8)));
		} catch (final GeneralSecurityException excepcion) {
			throw errorDeFirma(excepcion);
		}
	}

	public static String firmar(final String claveBase64, final String cadena) {
		try {
			final Mac mac = Mac.getInstance(ALGORITMO_HMAC);
			mac.init(new SecretKeySpec(Base64.getDecoder().decode(claveBase64), ALGORITMO_HMAC));
			return Base64.getEncoder().encodeToString(mac.doFinal(cadena.getBytes(StandardCharsets.UTF_8)));
		} catch (final GeneralSecurityException | IllegalArgumentException excepcion) {
			throw errorDeFirma(excepcion);
		}
	}

	private static NotaException errorDeFirma(final Exception causa) {
		return NotaException.crear(TipoError.INESPERADO, ClaveMensaje.ERROR_INESPERADO,
				"No se pudo firmar la solicitud al servicio de correo.", causa);
	}

}
