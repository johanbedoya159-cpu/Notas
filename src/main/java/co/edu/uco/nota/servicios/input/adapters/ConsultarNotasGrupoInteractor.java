package co.edu.uco.nota.servicios.input.adapters;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.dominio.comun.ConsultaPaginadaDominio;
import co.edu.uco.nota.dominio.comun.GrupoExistenteDominio;
import co.edu.uco.nota.dominio.nota.consultarnotasgrupo.ConsultarNotasGrupoDominio;
import co.edu.uco.nota.servicios.casosuso.nota.consultarnotasgrupo.ConsultarNotasGrupoCasoUso;
import co.edu.uco.nota.servicios.input.dto.request.ConsultarNotasGrupoRequestDTO;
import co.edu.uco.nota.servicios.input.dto.response.NotaResponseDTO;
import co.edu.uco.nota.servicios.input.dto.response.PaginaResponseDTO;
import co.edu.uco.nota.servicios.input.ports.ConsultarNotasGrupoInputPort;

public final class ConsultarNotasGrupoInteractor implements ConsultarNotasGrupoInputPort {

	private final ConsultarNotasGrupoCasoUso casoUso;

	public ConsultarNotasGrupoInteractor(final ConsultarNotasGrupoCasoUso casoUso) {
		this.casoUso = ObjetoHelper.requerirDependencia(casoUso, "el caso de uso de consultar notas del grupo");
	}

	@Override
	public PaginaResponseDTO<NotaResponseDTO> ejecutar(final ConsultarNotasGrupoRequestDTO solicitud) {
		final ConsultarNotasGrupoRequestDTO datos = ObjetoHelper.obtenerValorPorDefecto(solicitud,
				new ConsultarNotasGrupoRequestDTO(null, null, null));
		final ConsultarNotasGrupoDominio consulta = new ConsultarNotasGrupoDominio(
				new GrupoExistenteDominio(datos.grupo()), new ConsultaPaginadaDominio(datos.pagina(), datos.tamano()));
		return PaginaResponseDTO.desde(casoUso.ejecutar(consulta), NotaResponseDTO::desde);
	}

}
