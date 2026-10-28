package co.edu.uco.nota.inicializador.configuracion;

import java.net.http.HttpClient;
import java.time.Clock;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.edu.uco.nota.infraestructura.output.serviciosexternos.azure.ClienteHttpAzure;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.azure.IdentidadAdministradaAzure;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class ServiciosExternosConfiguracion {

	@Bean
	ClienteHttpAzure clienteHttpAzure() {
		return new ClienteHttpAzure(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build());
	}

	@Bean
	IdentidadAdministradaAzure identidadAdministradaAzure(final ClienteHttpAzure http, final JsonMapper json,
			@Value("${nota.azure.identidad.endpoint:}") final String endpoint,
			@Value("${nota.azure.identidad.encabezado:}") final String encabezado, final Clock reloj) {
		return new IdentidadAdministradaAzure(http, json, endpoint, encabezado, reloj);
	}

	@Bean(name = "ejecutorNotificaciones", destroyMethod = "close")
	ExecutorService ejecutorNotificaciones() {
		return Executors.newVirtualThreadPerTaskExecutor();
	}

}
