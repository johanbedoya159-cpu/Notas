package co.edu.uco.nota.servicios.input.adapters;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.dominio.comun.ConsultaPaginadaDominio;
import co.edu.uco.nota.dominio.comun.TextoBusquedaDominio;
import co.edu.uco.nota.dominio.grupo.consultargrupos.ConsultarGruposDominio;
import co.edu.uco.nota.servicios.casosuso.grupo.consultargrupos.ConsultarGruposCasoUso;
import co.edu.uco.nota.servicios.input.dto.request.ConsultarGruposRequestDTO;
import co.edu.uco.nota.servicios.input.dto.response.GrupoResponseDTO;
import co.edu.uco.nota.servicios.input.dto.response.PaginaResponseDTO;
import co.edu.uco.nota.servicios.input.ports.ConsultarGruposInputPort;

public final class ConsultarGruposInteractor implements ConsultarGruposInputPort {

	private final ConsultarGruposCasoUso casoUso;

	public ConsultarGruposInteractor(final ConsultarGruposCasoUso casoUso) {
		this.casoUso = ObjetoHelper.requerirDependencia(casoUso, "el caso de uso de consultar grupos");
	}

	@Override
	public PaginaResponseDTO<GrupoResponseDTO> ejecutar(final ConsultarGruposRequestDTO solicitud) {
		final ConsultarGruposRequestDTO datos = ObjetoHelper.obtenerValorPorDefecto(solicitud,
				new ConsultarGruposRequestDTO(null, null, null, null));
		final ConsultarGruposDominio consulta = new ConsultarGruposDominio(new TextoBusquedaDominio(datos.texto()),
				datos.docente(), new ConsultaPaginadaDominio(datos.pagina(), datos.tamano()));
		return PaginaResponseDTO.desde(casoUso.ejecutar(consulta), GrupoResponseDTO::desde);
	}

}
