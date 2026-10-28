package co.edu.uco.nota.dominio.estudiante.consultarmisnotas;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.excepcion.TipoError;
import co.edu.uco.nota.crosscutting.helper.TextoHelper;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.servicios.output.ports.repositorios.EstudianteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteEntidad;

public record ConsultarMisNotasDominio(String correo) {

	public ConsultarMisNotasDominio {
		correo = TextoHelper.aplicarTrim(correo);
	}

	public EstudianteEntidad validar(final EstudianteRepositorio repositorio) {
		if (TextoHelper.estaVacio(correo)) {
			throw NotaException.crear(TipoError.DATOS_INVALIDOS, ClaveMensaje.CORREO_OBLIGATORIO,
					"La sesión no trae el correo del estudiante.");
		}
		return repositorio.consultarPorCorreo(correo)
				.orElseThrow(() -> NotaException.crear(TipoError.NO_ENCONTRADO, ClaveMensaje.ESTUDIANTE_SIN_REGISTRO,
						"No hay un estudiante con el correo " + correo + ".", correo));
	}

}
