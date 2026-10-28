package co.edu.uco.nota.infraestructura.input.controladores;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.nota.infraestructura.input.autorizacion.AutorizacionInterceptor;
import co.edu.uco.nota.infraestructura.input.autorizacion.Rol;
import co.edu.uco.nota.infraestructura.input.autorizacion.UsuarioSesion;
import co.edu.uco.nota.infraestructura.input.controladores.respuesta.Respuesta;
import co.edu.uco.nota.infraestructura.input.controladores.respuesta.SesionRespuesta;
import co.edu.uco.nota.servicios.input.dto.request.ConsultarMisNotasRequestDTO;
import co.edu.uco.nota.servicios.input.dto.response.MiGrupoResponseDTO;
import co.edu.uco.nota.servicios.input.ports.ConsultarMisNotasInputPort;

@RestController
public class SesionControlador {

	private final ConsultarMisNotasInputPort consultarMisNotas;

	public SesionControlador(final ConsultarMisNotasInputPort consultarMisNotas) {
		this.consultarMisNotas = consultarMisNotas;
	}

	@GetMapping(Rol.RUTA_SESION)
	public ResponseEntity<Respuesta<SesionRespuesta>> consultarSesion(
			@RequestAttribute(AutorizacionInterceptor.ATRIBUTO_USUARIO) final UsuarioSesion usuario) {
		return ResponseEntity.ok(Respuesta.exitosa(SesionRespuesta.desde(usuario)));
	}

	@GetMapping(Rol.RUTA_MIS_NOTAS)
	@Transactional(readOnly = true)
	public ResponseEntity<Respuesta<List<MiGrupoResponseDTO>>> consultarMisNotas(
			@RequestAttribute(AutorizacionInterceptor.ATRIBUTO_USUARIO) final UsuarioSesion usuario) {
		return ResponseEntity
				.ok(Respuesta.exitosa(consultarMisNotas.ejecutar(new ConsultarMisNotasRequestDTO(usuario.correo()))));
	}

}
