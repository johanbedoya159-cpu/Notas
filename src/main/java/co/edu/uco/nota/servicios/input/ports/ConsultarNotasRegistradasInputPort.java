package co.edu.uco.nota.servicios.input.ports;

import co.edu.uco.nota.servicios.input.dto.request.ConsultarNotasRegistradasRequestDTO;
import co.edu.uco.nota.servicios.input.dto.response.PaginaResponseDTO;
import co.edu.uco.nota.servicios.input.dto.response.NotaEstudianteResponseDTO;

public interface ConsultarNotasRegistradasInputPort extends InputPortConRetorno<ConsultarNotasRegistradasRequestDTO, PaginaResponseDTO<NotaEstudianteResponseDTO>> {

}
