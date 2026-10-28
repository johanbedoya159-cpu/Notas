package co.edu.uco.nota.servicios.input.adapters;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.dominio.comun.ConsultaPaginadaDominio;
import co.edu.uco.nota.dominio.notaestudiante.consultarnotasregistradas.ConsultarNotasRegistradasDominio;
import co.edu.uco.nota.dominio.notaestudiante.registrarnotaestudiante.NotaDominio;
import co.edu.uco.nota.servicios.casosuso.notaestudiante.consultarnotasregistradas.ConsultarNotasRegistradasCasoUso;
import co.edu.uco.nota.servicios.input.dto.request.ConsultarNotasRegistradasRequestDTO;
import co.edu.uco.nota.servicios.input.dto.response.NotaEstudianteResponseDTO;
import co.edu.uco.nota.servicios.input.dto.response.PaginaResponseDTO;
import co.edu.uco.nota.servicios.input.ports.ConsultarNotasRegistradasInputPort;

public final class ConsultarNotasRegistradasInteractor implements ConsultarNotasRegistradasInputPort {

	private final ConsultarNotasRegistradasCasoUso casoUso;

	public ConsultarNotasRegistradasInteractor(final ConsultarNotasRegistradasCasoUso casoUso) {
		this.casoUso = ObjetoHelper.requerirDependencia(casoUso, "el caso de uso de consultar notas registradas");
	}

	@Override
	public PaginaResponseDTO<NotaEstudianteResponseDTO> ejecutar(
			final ConsultarNotasRegistradasRequestDTO solicitud) {
		final ConsultarNotasRegistradasRequestDTO datos = ObjetoHelper.obtenerValorPorDefecto(solicitud,
				new ConsultarNotasRegistradasRequestDTO(null, null, null));
		final ConsultarNotasRegistradasDominio consulta = new ConsultarNotasRegistradasDominio(
				new NotaDominio(datos.nota()), new ConsultaPaginadaDominio(datos.pagina(), datos.tamano()));
		return PaginaResponseDTO.desde(casoUso.ejecutar(consulta), NotaEstudianteResponseDTO::desde);
	}

}
