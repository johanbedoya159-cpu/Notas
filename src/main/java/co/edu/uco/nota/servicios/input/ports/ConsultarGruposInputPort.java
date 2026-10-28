package co.edu.uco.nota.servicios.input.ports;

import co.edu.uco.nota.servicios.input.dto.request.ConsultarGruposRequestDTO;
import co.edu.uco.nota.servicios.input.dto.response.PaginaResponseDTO;
import co.edu.uco.nota.servicios.input.dto.response.GrupoResponseDTO;

public interface ConsultarGruposInputPort extends InputPortConRetorno<ConsultarGruposRequestDTO, PaginaResponseDTO<GrupoResponseDTO>> {

}
