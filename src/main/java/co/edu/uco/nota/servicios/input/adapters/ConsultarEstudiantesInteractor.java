package co.edu.uco.nota.servicios.input.adapters;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.dominio.comun.ConsultaPaginadaDominio;
import co.edu.uco.nota.dominio.comun.TextoBusquedaDominio;
import co.edu.uco.nota.dominio.estudiante.consultarestudiantes.ConsultarEstudiantesDominio;
import co.edu.uco.nota.servicios.casosuso.estudiante.consultarestudiantes.ConsultarEstudiantesCasoUso;
import co.edu.uco.nota.servicios.input.dto.request.ConsultarEstudiantesRequestDTO;
import co.edu.uco.nota.servicios.input.dto.response.EstudianteResponseDTO;
import co.edu.uco.nota.servicios.input.dto.response.PaginaResponseDTO;
import co.edu.uco.nota.servicios.input.ports.ConsultarEstudiantesInputPort;

public final class ConsultarEstudiantesInteractor implements ConsultarEstudiantesInputPort {

	private final ConsultarEstudiantesCasoUso casoUso;

	public ConsultarEstudiantesInteractor(final ConsultarEstudiantesCasoUso casoUso) {
		this.casoUso = ObjetoHelper.requerirDependencia(casoUso, "el caso de uso de consultar estudiantes");
	}

	@Override
	public PaginaResponseDTO<EstudianteResponseDTO> ejecutar(final ConsultarEstudiantesRequestDTO solicitud) {
		final ConsultarEstudiantesRequestDTO datos = ObjetoHelper.obtenerValorPorDefecto(solicitud,
				new ConsultarEstudiantesRequestDTO(null, null, null));
		final ConsultarEstudiantesDominio consulta = new ConsultarEstudiantesDominio(
				new TextoBusquedaDominio(datos.texto()), new ConsultaPaginadaDominio(datos.pagina(), datos.tamano()));
		return PaginaResponseDTO.desde(casoUso.ejecutar(consulta), EstudianteResponseDTO::desde);
	}

}
