package co.edu.uco.nota.dominio.notaestudiante.registrarnotaestudiante;

import java.util.UUID;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.excepcion.TipoError;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.servicios.output.ports.repositorios.EstudianteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteEntidad;

public record EstudianteDominio(UUID id) {

	public EstudianteDominio {
		id = UUIDHelper.obtenerValorPorDefecto(id);
	}

	public void validarDatoObligatorio() {
		if (UUIDHelper.esPorDefecto(id)) {
			throw NotaException.crear(TipoError.DATOS_INVALIDOS, ClaveMensaje.ESTUDIANTE_OBLIGATORIO,
					"La solicitud no trae el identificador del estudiante.");
		}
	}

	public EstudianteEntidad validarExisteYEstaActivo(final EstudianteRepositorio repositorio) {
		final EstudianteEntidad estudiante = repositorio.consultarPorId(id)
				.orElseThrow(() -> NotaException.crear(TipoError.NO_ENCONTRADO, ClaveMensaje.ESTUDIANTE_NO_EXISTE,
						"No existe el estudiante " + id + "."));

		if (!EstudianteEntidad.ESTADO_ACTIVO.equals(estudiante.estado())) {
			throw NotaException.crear(TipoError.REGLA_NEGOCIO, ClaveMensaje.ESTUDIANTE_INACTIVO,
					"El estudiante " + id + " está en estado " + estudiante.estado() + ".");
		}

		return estudiante;
	}

}
