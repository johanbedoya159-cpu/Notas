package co.edu.uco.nota.inicializador.configuracion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Clock;
import java.util.concurrent.ExecutorService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.datosprueba.RepositoriosEnMemoria;
import co.edu.uco.nota.datosprueba.ServiciosExternosEnMemoria;
import co.edu.uco.nota.infraestructura.output.serviciosexternos.azure.ClienteHttpAzure;
import co.edu.uco.nota.servicios.input.dto.request.ObtenerMensajeRequestDTO;
import tools.jackson.databind.json.JsonMapper;

class ServiciosExternosConfiguracionTest {

	@Test
	@DisplayName("Arma el cliente HTTP, la identidad, el ejecutor de correos y el catálogo de mensajes")
	void armaBeans() {
		final ServiciosExternosConfiguracion externos = new ServiciosExternosConfiguracion();
		final ClienteHttpAzure http = externos.clienteHttpAzure();
		assertNotNull(http);
		assertFalse(externos.identidadAzure(http, JsonMapper.shared(), "", "", "", "", "", "", Clock.systemUTC())
				.estaDisponible());
		try (ExecutorService ejecutor = externos.ejecutorNotificaciones()) {
			assertFalse(ejecutor.isShutdown());
		}

		final MensajesConfiguracion mensajes = new MensajesConfiguracion();
		assertEquals("La nota se registró correctamente.",
				mensajes.obtenerMensajeInputPort(mensajes.obtenerMensajeCasoUso(ServiciosExternosEnMemoria.catalogoLocal()))
						.ejecutar(ObtenerMensajeRequestDTO.crear(ClaveMensaje.NOTA_REGISTRADA)));
	}

	@Test
	@DisplayName("Arma el registro de notas con sus repositorios, el catálogo de parámetros y las notificaciones")
	void armaRegistroDeNotas() {
		final RepositoriosEnMemoria memoria = new RepositoriosEnMemoria();
		final ServiciosExternosEnMemoria servicios = new ServiciosExternosEnMemoria();
		final NotaEstudianteConfiguracion configuracion = new NotaEstudianteConfiguracion();

		assertNotNull(configuracion.registrarNotaEstudianteInputPort(configuracion.registrarNotaEstudianteCasoUso(
				memoria.docente, memoria.estudiante, memoria.nota, memoria.grupo, memoria.estudianteGrupo,
				memoria.notaEstudiante, servicios.parametros, servicios.notificador, new RelojConfiguracion().reloj())));
	}

}
