package co.edu.uco.nota.servicios.input.adapters;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.dominio.comun.ConsultaPaginadaDominio;
import co.edu.uco.nota.dominio.comun.GrupoExistenteDominio;
import co.edu.uco.nota.dominio.grupo.consultarestudiantesgrupo.ConsultarEstudiantesGrupoDominio;
import co.edu.uco.nota.servicios.casosuso.grupo.consultarestudiantesgrupo.ConsultarEstudiantesGrupoCasoUso;
import co.edu.uco.nota.servicios.input.dto.request.ConsultarEstudiantesGrupoRequestDTO;
import co.edu.uco.nota.servicios.input.dto.response.EstudianteResponseDTO;
import co.edu.uco.nota.servicios.input.dto.response.PaginaResponseDTO;
import co.edu.uco.nota.servicios.input.ports.ConsultarEstudiantesGrupoInputPort;

public final class ConsultarEstudiantesGrupoInteractor implements ConsultarEstudiantesGrupoInputPort {

	private final ConsultarEstudiantesGrupoCasoUso casoUso;

	public ConsultarEstudiantesGrupoInteractor(final ConsultarEstudiantesGrupoCasoUso casoUso) {
		this.casoUso = ObjetoHelper.requerirDependencia(casoUso, "el caso de uso de consultar estudiantes del grupo");
	}

	@Override
	public PaginaResponseDTO<EstudianteResponseDTO> ejecutar(final ConsultarEstudiantesGrupoRequestDTO solicitud) {
		final ConsultarEstudiantesGrupoRequestDTO datos = ObjetoHelper.obtenerValorPorDefecto(solicitud,
				new ConsultarEstudiantesGrupoRequestDTO(null, null, null));
		final ConsultarEstudiantesGrupoDominio consulta = new ConsultarEstudiantesGrupoDominio(
				new GrupoExistenteDominio(datos.grupo()), new ConsultaPaginadaDominio(datos.pagina(), datos.tamano()));
		return PaginaResponseDTO.desde(casoUso.ejecutar(consulta),
				inscripcion -> EstudianteResponseDTO.desde(inscripcion.estudiante()));
	}

}
