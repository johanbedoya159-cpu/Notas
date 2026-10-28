package co.edu.uco.nota.servicios.input.dto.request;

import java.util.UUID;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;
import co.edu.uco.nota.crosscutting.paginacion.Paginacion;

public record ConsultarNotasRegistradasRequestDTO(UUID nota, Integer pagina, Integer tamano) {

	public ConsultarNotasRegistradasRequestDTO {
		nota = UUIDHelper.obtenerValorPorDefecto(nota);
		pagina = ObjetoHelper.obtenerValorPorDefecto(pagina, Paginacion.PAGINA_INICIAL);
		tamano = ObjetoHelper.obtenerValorPorDefecto(tamano, Paginacion.TAMANO_POR_DEFECTO);
	}

}
