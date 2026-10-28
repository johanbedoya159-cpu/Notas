package co.edu.uco.nota.servicios.input.ports;

import co.edu.uco.nota.servicios.input.dto.request.ConsultarEstudiantesRequestDTO;
import co.edu.uco.nota.servicios.input.dto.response.PaginaResponseDTO;
import co.edu.uco.nota.servicios.input.dto.response.EstudianteResponseDTO;

public interface ConsultarEstudiantesInputPort extends InputPortConRetorno<ConsultarEstudiantesRequestDTO, PaginaResponseDTO<EstudianteResponseDTO>> {

}
