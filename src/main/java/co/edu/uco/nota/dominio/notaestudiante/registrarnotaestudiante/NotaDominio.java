package co.edu.uco.nota.dominio.notaestudiante.registrarnotaestudiante;

import java.time.Instant;
import java.util.UUID;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.excepcion.TipoError;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.servicios.output.ports.repositorios.NotaRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEntidad;

public record NotaDominio(UUID id) {

	public NotaDominio {
		id = UUIDHelper.obtenerValorPorDefecto(id);
	}

	public void validarDatoObligatorio() {
		if (UUIDHelper.esPorDefecto(id)) {
			throw NotaException.crear(TipoError.DATOS_INVALIDOS, ClaveMensaje.NOTA_OBLIGATORIA,
					"La solicitud no trae el identificador de la nota a evaluar.");
		}
	}

	public NotaEntidad validarExiste(final NotaRepositorio repositorio) {
		return repositorio.consultarPorId(id).orElseThrow(() -> NotaException.crear(TipoError.NO_ENCONTRADO,
				ClaveMensaje.NOTA_NO_EXISTE, "No existe la nota " + id + "."));
	}

	public void validarFechaDeRegistroVigente(final NotaEntidad nota, final Instant ahora) {
		if (ahora.isBefore(nota.fechaInicioRegistro())) {
			throw NotaException.crear(TipoError.REGLA_NEGOCIO, ClaveMensaje.REGISTRO_DE_NOTA_NO_HA_INICIADO,
					"El registro de la nota " + id + " abre el " + nota.fechaInicioRegistro() + ".",
					nota.fechaInicioRegistro());
		}

		if (!ahora.isBefore(nota.fechaFinRegistro())) {
			throw NotaException.crear(TipoError.REGLA_NEGOCIO, ClaveMensaje.REGISTRO_DE_NOTA_FINALIZADO,
					"El registro de la nota " + id + " cerró el " + nota.fechaFinRegistro() + ".",
					nota.fechaFinRegistro());
		}
	}

}
