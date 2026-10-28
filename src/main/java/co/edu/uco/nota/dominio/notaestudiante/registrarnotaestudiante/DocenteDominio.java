package co.edu.uco.nota.dominio.notaestudiante.registrarnotaestudiante;

import java.util.UUID;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.excepcion.TipoError;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.servicios.output.ports.repositorios.DocenteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.DocenteEntidad;

public record DocenteDominio(UUID id) {

	public DocenteDominio {
		id = UUIDHelper.obtenerValorPorDefecto(id);
	}

	public void validarDatoObligatorio() {
		if (UUIDHelper.esPorDefecto(id)) {
			throw NotaException.crear(TipoError.DATOS_INVALIDOS, ClaveMensaje.DOCENTE_OBLIGATORIO,
					"La solicitud no trae el identificador del docente.");
		}
	}

	public DocenteEntidad validarExisteYEstaActivo(final DocenteRepositorio repositorio) {
		final DocenteEntidad docente = repositorio.consultarPorId(id)
				.orElseThrow(() -> NotaException.crear(TipoError.NO_ENCONTRADO, ClaveMensaje.DOCENTE_NO_EXISTE,
						"No existe el docente " + id + "."));

		if (!DocenteEntidad.ESTADO_ACTIVO.equals(docente.estado())) {
			throw NotaException.crear(TipoError.REGLA_NEGOCIO, ClaveMensaje.DOCENTE_INACTIVO,
					"El docente " + id + " está en estado " + docente.estado() + ".");
		}

		return docente;
	}

}
