package co.edu.uco.nota.crosscutting.helper;

import java.security.SecureRandom;
import java.util.UUID;

public final class UUIDHelper {

	public static final UUID UUID_POR_DEFECTO = new UUID(0L, 0L);

	private static final SecureRandom ALEATORIO = new SecureRandom();
	private static final long VERSION_7 = 0x7000L;
	private static final long MASCARA_12_BITS = 0x0FFFL;
	private static final long MASCARA_62_BITS = 0x3FFFFFFFFFFFFFFFL;
	private static final long VARIANTE_IETF = 0x8000000000000000L;

	private UUIDHelper() {
	}

	public static UUID obtenerValorPorDefecto(final UUID valor) {
		return ObjetoHelper.obtenerValorPorDefecto(valor, UUID_POR_DEFECTO);
	}

	public static boolean esPorDefecto(final UUID valor) {
		return UUID_POR_DEFECTO.equals(obtenerValorPorDefecto(valor));
	}

	public static UUID convertir(final String texto) {
		try {
			return UUID.fromString(TextoHelper.aplicarTrim(texto));
		} catch (final IllegalArgumentException excepcion) {
			return UUID_POR_DEFECTO;
		}
	}

	public static UUID generarNuevo() {
		final long milisegundos = System.currentTimeMillis();
		final long masSignificativos = (milisegundos << 16) | VERSION_7 | (ALEATORIO.nextLong() & MASCARA_12_BITS);
		final long menosSignificativos = (ALEATORIO.nextLong() & MASCARA_62_BITS) | VARIANTE_IETF;
		return new UUID(masSignificativos, menosSignificativos);
	}

}
