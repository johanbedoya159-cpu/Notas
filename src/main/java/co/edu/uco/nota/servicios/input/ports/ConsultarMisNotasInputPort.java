package co.edu.uco.nota.servicios.input.ports;

import java.util.List;

import co.edu.uco.nota.servicios.input.dto.request.ConsultarMisNotasRequestDTO;
import co.edu.uco.nota.servicios.input.dto.response.MiGrupoResponseDTO;

public interface ConsultarMisNotasInputPort
		extends InputPortConRetorno<ConsultarMisNotasRequestDTO, List<MiGrupoResponseDTO>> {

}
