package co.edu.uco.nota.servicios.input.dto.request;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.TextoHelper;
import co.edu.uco.nota.crosscutting.paginacion.Paginacion;

public record ConsultarEstudiantesRequestDTO(String texto, Integer pagina, Integer tamano) {

	public ConsultarEstudiantesRequestDTO {
		texto = TextoHelper.aplicarTrim(texto);
		pagina = ObjetoHelper.obtenerValorPorDefecto(pagina, Paginacion.PAGINA_INICIAL);
		tamano = ObjetoHelper.obtenerValorPorDefecto(tamano, Paginacion.TAMANO_POR_DEFECTO);
	}

}
