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
import co.edu.uco.nota.servicios.input.dto.request.ConsultarEstudiantesGrupoRequestDTO;
import co.edu.uco.nota.servicios.input.dto.request.ConsultarGruposRequestDTO;
import co.edu.uco.nota.servicios.input.dto.request.ConsultarNotasGrupoRequestDTO;
import co.edu.uco.nota.servicios.input.dto.response.EstudianteResponseDTO;
import co.edu.uco.nota.servicios.input.dto.response.GrupoResponseDTO;
import co.edu.uco.nota.servicios.input.dto.response.NotaResponseDTO;
import co.edu.uco.nota.servicios.input.dto.response.PaginaResponseDTO;
import co.edu.uco.nota.servicios.input.ports.ConsultarEstudiantesGrupoInputPort;
import co.edu.uco.nota.servicios.input.ports.ConsultarGruposInputPort;
import co.edu.uco.nota.servicios.input.ports.ConsultarNotasGrupoInputPort;

@RestController
@RequestMapping("/api/v1/grupos")
public class GrupoControlador {

	private final ConsultarGruposInputPort consultarGrupos;
	private final ConsultarEstudiantesGrupoInputPort consultarEstudiantesGrupo;
	private final ConsultarNotasGrupoInputPort consultarNotasGrupo;

	public GrupoControlador(final ConsultarGruposInputPort consultarGrupos,
			final ConsultarEstudiantesGrupoInputPort consultarEstudiantesGrupo,
			final ConsultarNotasGrupoInputPort consultarNotasGrupo) {
		this.consultarGrupos = consultarGrupos;
		this.consultarEstudiantesGrupo = consultarEstudiantesGrupo;
		this.consultarNotasGrupo = consultarNotasGrupo;
	}

	@GetMapping
	@Transactional(readOnly = true)
	public ResponseEntity<Respuesta<PaginaResponseDTO<GrupoResponseDTO>>> consultar(
			@RequestParam(required = false) final String texto, @RequestParam(required = false) final UUID docente,
			@RequestParam(required = false) final Integer pagina,
			@RequestParam(required = false) final Integer tamano) {
		return ResponseEntity.ok(Respuesta
				.exitosa(consultarGrupos.ejecutar(new ConsultarGruposRequestDTO(texto, docente, pagina, tamano))));
	}

	@GetMapping("/{grupo}/estudiantes")
	@Transactional(readOnly = true)
	public ResponseEntity<Respuesta<PaginaResponseDTO<EstudianteResponseDTO>>> consultarEstudiantes(
			@PathVariable final UUID grupo, @RequestParam(required = false) final Integer pagina,
			@RequestParam(required = false) final Integer tamano) {
		return ResponseEntity.ok(Respuesta.exitosa(consultarEstudiantesGrupo
				.ejecutar(new ConsultarEstudiantesGrupoRequestDTO(grupo, pagina, tamano))));
	}

	@GetMapping("/{grupo}/notas")
	@Transactional(readOnly = true)
	public ResponseEntity<Respuesta<PaginaResponseDTO<NotaResponseDTO>>> consultarNotas(
			@PathVariable final UUID grupo, @RequestParam(required = false) final Integer pagina,
			@RequestParam(required = false) final Integer tamano) {
		return ResponseEntity.ok(Respuesta
				.exitosa(consultarNotasGrupo.ejecutar(new ConsultarNotasGrupoRequestDTO(grupo, pagina, tamano))));
	}

}
