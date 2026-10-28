package co.edu.uco.nota.infraestructura.input.controladores;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.nota.infraestructura.input.controladores.respuesta.Respuesta;
import co.edu.uco.nota.servicios.input.dto.request.ConsultarNotasRegistradasRequestDTO;
import co.edu.uco.nota.servicios.input.dto.response.NotaEstudianteResponseDTO;
import co.edu.uco.nota.servicios.input.dto.response.PaginaResponseDTO;
import co.edu.uco.nota.servicios.input.ports.ConsultarNotasRegistradasInputPort;

@RestController
@RequestMapping("/api/v1/notas")
public class NotaControlador {

	private final ConsultarNotasRegistradasInputPort consultarNotasRegistradas;

	public NotaControlador(final ConsultarNotasRegistradasInputPort consultarNotasRegistradas) {
		this.consultarNotasRegistradas = consultarNotasRegistradas;
	}

	@GetMapping("/{nota}/estudiantes")
	@Transactional(readOnly = true)
	public ResponseEntity<Respuesta<PaginaResponseDTO<NotaEstudianteResponseDTO>>> consultarRegistradas(
			@PathVariable final UUID nota, @RequestParam(required = false) final Integer pagina,
			@RequestParam(required = false) final Integer tamano) {
		return ResponseEntity.ok(Respuesta.exitosa(consultarNotasRegistradas
				.ejecutar(new ConsultarNotasRegistradasRequestDTO(nota, pagina, tamano))));
	}

}
