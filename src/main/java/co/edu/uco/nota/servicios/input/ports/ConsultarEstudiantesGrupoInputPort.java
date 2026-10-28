package co.edu.uco.nota.servicios.input.ports;

import co.edu.uco.nota.servicios.input.dto.request.ConsultarEstudiantesGrupoRequestDTO;
import co.edu.uco.nota.servicios.input.dto.response.PaginaResponseDTO;
import co.edu.uco.nota.servicios.input.dto.response.EstudianteResponseDTO;

public interface ConsultarEstudiantesGrupoInputPort extends InputPortConRetorno<ConsultarEstudiantesGrupoRequestDTO, PaginaResponseDTO<EstudianteResponseDTO>> {

}
