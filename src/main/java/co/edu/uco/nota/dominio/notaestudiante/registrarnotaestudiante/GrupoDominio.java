package co.edu.uco.nota.dominio.notaestudiante.registrarnotaestudiante;

import java.util.UUID;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.excepcion.TipoError;
import co.edu.uco.nota.crosscutting.helper.UUIDHelper;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.servicios.output.ports.repositorios.GrupoRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.DocenteEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;

public record GrupoDominio(UUID id) {

	public GrupoDominio {
		id = UUIDHelper.obtenerValorPorDefecto(id);
	}

	public GrupoEntidad validarExisteYEstaVigente(final GrupoRepositorio repositorio) {
		final GrupoEntidad grupo = repositorio.consultarPorId(id)
				.orElseThrow(() -> NotaException.crear(TipoError.NO_ENCONTRADO, ClaveMensaje.GRUPO_NO_EXISTE,
						"No existe el grupo " + id + "."));

		if (!GrupoEntidad.ESTADO_VIGENTE.equals(grupo.estado())) {
			throw NotaException.crear(TipoError.REGLA_NEGOCIO, ClaveMensaje.GRUPO_NO_VIGENTE,
					"El grupo " + id + " está en estado " + grupo.estado() + ".");
		}

		return grupo;
	}

	public void validarDocenteAsignado(final GrupoEntidad grupo, final DocenteEntidad docente) {
		if (!grupo.docente().id().equals(docente.id())) {
			throw NotaException.crear(TipoError.REGLA_NEGOCIO, ClaveMensaje.DOCENTE_NO_ASIGNADO_AL_GRUPO,
					"El docente " + docente.id() + " no está asignado al grupo " + grupo.id() + ".");
		}
	}

}
