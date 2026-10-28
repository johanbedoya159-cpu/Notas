package co.edu.uco.nota.servicios.input.ports;

import co.edu.uco.nota.servicios.input.dto.request.ConsultarNotasGrupoRequestDTO;
import co.edu.uco.nota.servicios.input.dto.response.PaginaResponseDTO;
import co.edu.uco.nota.servicios.input.dto.response.NotaResponseDTO;

public interface ConsultarNotasGrupoInputPort extends InputPortConRetorno<ConsultarNotasGrupoRequestDTO, PaginaResponseDTO<NotaResponseDTO>> {

}
