package co.edu.uco.nota.servicios.output.ports.serviciosexternos.secretos;

import java.util.Optional;

public interface BaulSecretosServicio {

	Optional<String> obtenerSecreto(String nombre);

}
