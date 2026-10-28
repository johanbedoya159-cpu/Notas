package co.edu.uco.nota.datosprueba;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

public final class RelojMovil extends Clock {

	private Instant ahora;

	public RelojMovil(final Instant inicio) {
		this.ahora = inicio;
	}

	public void avanzar(final Duration tiempo) {
		ahora = ahora.plus(tiempo);
	}

	@Override
	public ZoneId getZone() {
		return ZoneOffset.UTC;
	}

	@Override
	public Clock withZone(final ZoneId zona) {
		return this;
	}

	@Override
	public Instant instant() {
		return ahora;
	}

}
