package co.edu.uco.nota.infraestructura.output.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.uco.nota.datosprueba.DatosPrueba;
import co.edu.uco.nota.datosprueba.RelojMovil;

class CacheEnMemoriaTest {

	private final RelojMovil reloj = new RelojMovil(DatosPrueba.AHORA);
	private final AtomicInteger consultas = new AtomicInteger();

	private String fuente() {
		return "valor-" + consultas.incrementAndGet();
	}

	@Test
	@DisplayName("Va a la fuente solo cuando no tiene el dato y luego responde desde la memoria")
	void aciertoYFallo() {
		final CacheEnMemoria<String> cache = new CacheEnMemoria<>("prueba", Duration.ofMinutes(5), 10, reloj);

		assertEquals("valor-1", cache.obtener("a", this::fuente));
		assertEquals("valor-1", cache.obtener("a", this::fuente));

		assertEquals(1, consultas.get());
		assertEquals(1, cache.getAciertos());
		assertEquals(1, cache.getFallos());
	}

	@Test
	@DisplayName("Vuelve a la fuente cuando vence el tiempo de vida o se invalida")
	void venceEInvalida() {
		final CacheEnMemoria<String> cache = new CacheEnMemoria<>("prueba", Duration.ofMinutes(5), 10, reloj);
		cache.obtener("a", this::fuente);

		reloj.avanzar(Duration.ofMinutes(5));
		assertEquals("valor-2", cache.obtener("a", this::fuente));

		cache.invalidar();
		assertEquals(0, cache.getTamano());
		assertEquals("valor-3", cache.obtener("a", this::fuente));
	}

	@Test
	@DisplayName("No guarda los valores que la regla no permite")
	void noGuardaLoQueNoDebe() {
		final CacheEnMemoria<String> cache = new CacheEnMemoria<>("prueba", Duration.ofMinutes(5), 10, reloj);

		cache.obtener("a", this::fuente, valor -> false);
		cache.obtener("a", this::fuente, valor -> false);

		assertEquals(2, consultas.get());
		assertEquals(0, cache.getTamano());
	}

	@Test
	@DisplayName("Respeta la capacidad: primero saca lo vencido y luego lo más antiguo")
	void respetaCapacidad() {
		final CacheEnMemoria<String> cache = new CacheEnMemoria<>("prueba", Duration.ofMinutes(5), 2, reloj);
		cache.obtener("a", this::fuente);
		reloj.avanzar(Duration.ofMinutes(1));
		cache.obtener("b", this::fuente);
		reloj.avanzar(Duration.ofMinutes(1));

		cache.obtener("c", this::fuente);
		assertEquals(2, cache.getTamano());
		assertEquals("valor-2", cache.obtener("b", this::fuente));

		reloj.avanzar(Duration.ofMinutes(10));
		cache.obtener("d", this::fuente);
		assertEquals(1, cache.getTamano());
	}

}
