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
import co.edu.uco.nota.infraestructura.output.serviciosexternos.azure.CredencialesAzure;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.azure.IdentidadAzure;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class ServiciosExternosConfiguracion {

	@Bean
	ClienteHttpAzure clienteHttpAzure() {
		return new ClienteHttpAzure(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build());
	}

	@Bean
	IdentidadAzure identidadAzure(final ClienteHttpAzure http, final JsonMapper json,
			@Value("${nota.azure.identidad.endpoint:}") final String endpoint,
			@Value("${nota.azure.identidad.encabezado:}") final String encabezado,
			@Value("${nota.azure.aplicacion.autoridad:}") final String autoridad,
			@Value("${nota.azure.aplicacion.inquilino:}") final String inquilino,
			@Value("${nota.azure.aplicacion.cliente:}") final String cliente,
			@Value("${nota.azure.aplicacion.secreto:}") final String secreto, final Clock reloj) {
		return new IdentidadAzure(http, json,
				new CredencialesAzure(endpoint, encabezado, autoridad, inquilino, cliente, secreto), reloj);
	}

	@Bean(name = "ejecutorNotificaciones", destroyMethod = "close")
	ExecutorService ejecutorNotificaciones() {
		return Executors.newVirtualThreadPerTaskExecutor();
	}

}
