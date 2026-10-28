package co.edu.uco.nota.infraestructura.output.serviciosexternos.notificaciones.dto;

import java.util.List;

public record DestinatariosCorreoDTO(List<DireccionCorreoDTO> to) {

	public DestinatariosCorreoDTO {
		to = List.copyOf(to);
	}

}
