package co.edu.uco.nota.infraestructura.input.controladores;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.nota.infraestructura.input.controladores.respuesta.Respuesta;
import co.edu.uco.nota.servicios.input.dto.request.ConsultarEstudiantesRequestDTO;
import co.edu.uco.nota.servicios.input.dto.response.EstudianteResponseDTO;
import co.edu.uco.nota.servicios.input.dto.response.PaginaResponseDTO;
import co.edu.uco.nota.servicios.input.ports.ConsultarEstudiantesInputPort;

@RestController
@RequestMapping("/api/v1/estudiantes")
public class EstudianteControlador {

	private final ConsultarEstudiantesInputPort consultarEstudiantes;

	public EstudianteControlador(final ConsultarEstudiantesInputPort consultarEstudiantes) {
		this.consultarEstudiantes = consultarEstudiantes;
	}

	@GetMapping
	@Transactional(readOnly = true)
	public ResponseEntity<Respuesta<PaginaResponseDTO<EstudianteResponseDTO>>> consultar(
			@RequestParam(required = false) final String texto, @RequestParam(required = false) final Integer pagina,
			@RequestParam(required = false) final Integer tamano) {
		return ResponseEntity.ok(Respuesta.exitosa(
				consultarEstudiantes.ejecutar(new ConsultarEstudiantesRequestDTO(texto, pagina, tamano))));
	}

}
