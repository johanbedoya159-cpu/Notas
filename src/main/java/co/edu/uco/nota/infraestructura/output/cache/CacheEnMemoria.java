package co.edu.uco.nota.infraestructura.output.cache;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CacheEnMemoria<V> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CacheEnMemoria.class);

	private final String nombre;
	private final Duration tiempoDeVida;
	private final int capacidadMaxima;
	private final Clock reloj;
	private final Map<String, Entrada<V>> entradas = new ConcurrentHashMap<>();
	private final AtomicLong aciertos = new AtomicLong();
	private final AtomicLong fallos = new AtomicLong();

	public CacheEnMemoria(final String nombre, final Duration tiempoDeVida, final int capacidadMaxima,
			final Clock reloj) {
		this.nombre = nombre;
		this.tiempoDeVida = tiempoDeVida;
		this.capacidadMaxima = Math.max(1, capacidadMaxima);
		this.reloj = reloj;
	}

	public V obtener(final String clave, final Supplier<V> origen) {
		return obtener(clave, origen, valor -> true);
	}

	public V obtener(final String clave, final Supplier<V> origen, final Predicate<V> sePuedeGuardar) {
		final Instant ahora = reloj.instant();
		final Entrada<V> entrada = entradas.get(clave);
		if (entrada != null && ahora.isBefore(entrada.venceEn())) {
			aciertos.incrementAndGet();
			LOGGER.debug("Caché {}: acierto para {}.", nombre, clave);
			return entrada.valor();
		}

		fallos.incrementAndGet();
		LOGGER.debug("Caché {}: fallo para {}; se consulta la fuente.", nombre, clave);
		final V valor = origen.get();
		if (sePuedeGuardar.test(valor)) {
			liberarEspacio(ahora);
			entradas.put(clave, new Entrada<>(valor, ahora.plus(tiempoDeVida)));
		}
		return valor;
	}


	public void invalidar() {
		entradas.clear();
	}

	public long getAciertos() {
		return aciertos.get();
	}

	public long getFallos() {
		return fallos.get();
	}

	public int getTamano() {
		return entradas.size();
	}

	private void liberarEspacio(final Instant ahora) {
		if (entradas.size() < capacidadMaxima) {
			return;
		}
		entradas.entrySet().removeIf(registro -> !ahora.isBefore(registro.getValue().venceEn()));
		if (entradas.size() >= capacidadMaxima) {
			entradas.entrySet().stream().min(Map.Entry.comparingByValue((a, b) -> a.venceEn().compareTo(b.venceEn())))
					.ifPresent(masAntigua -> entradas.remove(masAntigua.getKey()));
		}
	}

	private record Entrada<V>(V valor, Instant venceEn) {
	}

}
