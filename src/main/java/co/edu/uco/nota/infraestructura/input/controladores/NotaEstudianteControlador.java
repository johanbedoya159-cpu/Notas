package co.edu.uco.nota.infraestructura.input.controladores;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.infraestructura.input.controladores.respuesta.CatalogoMensajes;
import co.edu.uco.nota.infraestructura.input.controladores.respuesta.Respuesta;
import co.edu.uco.nota.servicios.input.dto.request.RegistrarNotaEstudianteRequestDTO;
import co.edu.uco.nota.servicios.input.ports.RegistrarNotaEstudianteInputPort;

@RestController
@RequestMapping("/api/v1/notas-estudiantes")
public class NotaEstudianteControlador {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotaEstudianteControlador.class);

	private final RegistrarNotaEstudianteInputPort registrarNotaEstudiante;
	private final CatalogoMensajes catalogo;

	public NotaEstudianteControlador(final RegistrarNotaEstudianteInputPort registrarNotaEstudiante,
			final CatalogoMensajes catalogo) {
		this.registrarNotaEstudiante = registrarNotaEstudiante;
		this.catalogo = catalogo;
	}

	@PostMapping
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<Respuesta<List<Object>>> registrar(
			@RequestBody final RegistrarNotaEstudianteRequestDTO solicitud) {
		LOGGER.info("Inicia el registro de una nota de estudiante.");
		registrarNotaEstudiante.ejecutar(solicitud);
		LOGGER.info("Nota de estudiante registrada.");
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(Respuesta.exitosa(catalogo.obtener(ClaveMensaje.NOTA_REGISTRADA)));
	}

}
