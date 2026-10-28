package co.edu.uco.nota.servicios.input.dto.request;

import co.edu.uco.nota.crosscutting.helper.TextoHelper;

public record ConsultarMisNotasRequestDTO(String correo) {

	public ConsultarMisNotasRequestDTO {
		correo = TextoHelper.aplicarTrim(correo);
	}

}
