package co.edu.uco.nota.dominio.comun;

import java.util.UUID;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.excepcion.TipoError;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.servicios.output.ports.repositorios.GrupoRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;

public record GrupoExistenteDominio(UUID id) {

	public GrupoExistenteDominio {
		id = UUIDHelper.obtenerValorPorDefecto(id);
	}

	public void validarDatoObligatorio() {
		if (UUIDHelper.esPorDefecto(id)) {
			throw NotaException.crear(TipoError.DATOS_INVALIDOS, ClaveMensaje.GRUPO_OBLIGATORIO,
					"La consulta no trae el identificador del grupo.");
		}
	}

	public GrupoEntidad validarExiste(final GrupoRepositorio repositorio) {
		return repositorio.consultarPorId(id).orElseThrow(() -> NotaException.crear(TipoError.NO_ENCONTRADO,
				ClaveMensaje.GRUPO_NO_EXISTE, "No existe el grupo " + id + "."));
	}

}
