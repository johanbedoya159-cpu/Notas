package co.edu.uco.nota.servicios.casosuso.notaestudiante.registrarnotaestudiante.impl;

import java.time.Clock;
import java.time.Instant;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.dominio.notaestudiante.registrarnotaestudiante.EstudianteGrupoDominio;
import co.edu.uco.nota.dominio.notaestudiante.registrarnotaestudiante.GrupoDominio;
import co.edu.uco.nota.dominio.notaestudiante.registrarnotaestudiante.RegistrarNotaEstudianteDominio;
import co.edu.uco.nota.servicios.casosuso.notaestudiante.registrarnotaestudiante.RegistrarNotaEstudianteCasoUso;
import co.edu.uco.nota.servicios.output.ports.repositorios.DocenteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.EstudianteGrupoRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.EstudianteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.GrupoRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.NotaEstudianteRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.NotaRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.DocenteEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteGrupoEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEntidad;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.catalogos.CatalogoParametrosServicio;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.notificaciones.NotificacionServicio;

public final class RegistrarNotaEstudianteCasoUsoImpl implements RegistrarNotaEstudianteCasoUso {

	private final DocenteRepositorio docenteRepositorio;
	private final EstudianteRepositorio estudianteRepositorio;
	private final NotaRepositorio notaRepositorio;
	private final GrupoRepositorio grupoRepositorio;
	private final EstudianteGrupoRepositorio estudianteGrupoRepositorio;
	private final NotaEstudianteRepositorio notaEstudianteRepositorio;
	private final CatalogoParametrosServicio parametros;
	private final NotificacionServicio notificaciones;
	private final Clock reloj;

	public RegistrarNotaEstudianteCasoUsoImpl(final RepositoriosRegistroNota repositorios,
			final ServiciosRegistroNota servicios, final Clock reloj) {
		final RepositoriosRegistroNota requeridos = ObjetoHelper.requerirDependencia(repositorios,
				"los repositorios del registro de nota");
		this.docenteRepositorio = ObjetoHelper.requerirDependencia(requeridos.docente(), "el repositorio de docentes");
		this.estudianteRepositorio = ObjetoHelper.requerirDependencia(requeridos.estudiante(),
				"el repositorio de estudiantes");
		this.notaRepositorio = ObjetoHelper.requerirDependencia(requeridos.nota(), "el repositorio de notas");
		this.grupoRepositorio = ObjetoHelper.requerirDependencia(requeridos.grupo(), "el repositorio de grupos");
		this.estudianteGrupoRepositorio = ObjetoHelper.requerirDependencia(requeridos.estudianteGrupo(),
				"el repositorio de inscripciones");
		this.notaEstudianteRepositorio = ObjetoHelper.requerirDependencia(requeridos.notaEstudiante(),
				"el repositorio de notas de estudiantes");
		final ServiciosRegistroNota externos = ObjetoHelper.requerirDependencia(servicios,
				"los servicios externos del registro de nota");
		this.parametros = ObjetoHelper.requerirDependencia(externos.parametros(), "el catálogo de parámetros");
		this.notificaciones = ObjetoHelper.requerirDependencia(externos.notificaciones(),
				"el servicio de notificaciones");
		this.reloj = ObjetoHelper.requerirDependencia(reloj, "el reloj");
	}

	@Override
	public void ejecutar(final RegistrarNotaEstudianteDominio datos) {
		final RegistrarNotaEstudianteDominio registro = ObjetoHelper.obtenerValorPorDefecto(datos,
				RegistrarNotaEstudianteDominio.crear(null, null, null, null));
		registro.validarDatos(parametros);

		final DocenteEntidad docente = registro.docente().validarExisteYEstaActivo(docenteRepositorio);
		final EstudianteEntidad estudiante = registro.estudiante().validarExisteYEstaActivo(estudianteRepositorio);
		final NotaEntidad nota = registro.nota().validarExiste(notaRepositorio);

		final GrupoDominio grupoDominio = new GrupoDominio(nota.grupo().id());
		final GrupoEntidad grupo = grupoDominio.validarExisteYEstaVigente(grupoRepositorio);
		grupoDominio.validarDocenteAsignado(grupo, docente);

		final EstudianteGrupoEntidad estudianteGrupo = new EstudianteGrupoDominio(estudiante, grupo)
				.validarEstudiantePerteneceAlGrupo(estudianteGrupoRepositorio);

		final Instant ahora = reloj.instant();
		registro.nota().validarFechaDeRegistroVigente(nota, ahora);
		registro.notaEstudiante().validarEstudianteSinNotaRegistrada(notaEstudianteRepositorio, estudianteGrupo,
				nota);

		notaEstudianteRepositorio.crear(registro.notaEstudiante().crearRegistro(estudianteGrupo, nota, ahora));
		registro.notaEstudiante().crearNotificacion(estudiante, nota, grupo, parametros)
				.ifPresent(notificaciones::notificar);
	}

}
