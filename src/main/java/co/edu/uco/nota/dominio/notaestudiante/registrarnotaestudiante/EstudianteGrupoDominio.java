package co.edu.uco.nota.dominio.notaestudiante.registrarnotaestudiante;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.excepcion.TipoError;
import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.crosscutting.paginacion.Paginacion;
import co.edu.uco.nota.servicios.output.ports.repositorios.EstudianteGrupoRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.EstudianteGrupoCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteGrupoEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;

public record EstudianteGrupoDominio(EstudianteEntidad estudiante, GrupoEntidad grupo) {

	public EstudianteGrupoDominio {
		estudiante = ObjetoHelper.obtenerValorPorDefecto(estudiante, EstudianteEntidad.vacio());
		grupo = ObjetoHelper.obtenerValorPorDefecto(grupo, GrupoEntidad.vacio());
	}

	public EstudianteGrupoEntidad validarEstudiantePerteneceAlGrupo(final EstudianteGrupoRepositorio repositorio) {
		return repositorio.consultar(new EstudianteGrupoCriterio(estudiante.id(), grupo.id(), Paginacion.unico()))
				.contenido().stream().findFirst()
				.orElseThrow(() -> NotaException.crear(TipoError.REGLA_NEGOCIO,
						ClaveMensaje.ESTUDIANTE_NO_PERTENECE_AL_GRUPO,
						"El estudiante " + estudiante.id() + " no está inscrito en el grupo " + grupo.id() + "."));
	}

}
