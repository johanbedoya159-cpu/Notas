package co.edu.uco.nota.servicios.casosuso.notaestudiante.registrarnotaestudiante.impl;

import static co.edu.uco.nota.datosprueba.DatosPrueba.AHORA;
import static co.edu.uco.nota.datosprueba.DatosPrueba.DOCENTE;
import static co.edu.uco.nota.datosprueba.DatosPrueba.ESTUDIANTE;
import static co.edu.uco.nota.datosprueba.DatosPrueba.NOTA;
import static co.edu.uco.nota.datosprueba.DatosPrueba.OTRO_DOCENTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.excepcion.TipoError;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.crosscutting.parametros.ClaveParametro;
import co.edu.uco.nota.datosprueba.DatosPrueba;
import co.edu.uco.nota.datosprueba.RepositoriosEnMemoria;
import co.edu.uco.nota.datosprueba.ServiciosExternosEnMemoria;
import co.edu.uco.nota.dominio.notaestudiante.registrarnotaestudiante.RegistrarNotaEstudianteDominio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEstudianteEntidad;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.notificaciones.NotificacionCorreo;

class RegistrarNotaEstudianteCasoUsoImplTest {

	private RepositoriosEnMemoria memoria;
	private ServiciosExternosEnMemoria servicios;
	private RegistrarNotaEstudianteCasoUsoImpl casoUso;

	@BeforeEach
	void preparar() {
		memoria = new RepositoriosEnMemoria();
		servicios = new ServiciosExternosEnMemoria();
		final GrupoEntidad grupo = DatosPrueba.grupo(DOCENTE, "VIGENTE");
		memoria.docentes.put(DOCENTE, DatosPrueba.docente(DOCENTE, "ACTIVO"));
		memoria.docentes.put(OTRO_DOCENTE, DatosPrueba.docente(OTRO_DOCENTE, "ACTIVO"));
		memoria.estudiantes.put(ESTUDIANTE, DatosPrueba.estudiante("ACTIVO"));
		memoria.grupos.put(grupo.id(), grupo);
		memoria.inscripciones.add(DatosPrueba.inscripcion(grupo));
		memoria.notas.put(NOTA, DatosPrueba.notaAbierta(grupo));
		casoUso = new RegistrarNotaEstudianteCasoUsoImpl(new RepositoriosRegistroNota(memoria.docente,
				memoria.estudiante, memoria.nota, memoria.grupo, memoria.estudianteGrupo, memoria.notaEstudiante),
				new ServiciosRegistroNota(servicios.parametros, servicios.notificador), Clock.fixed(AHORA, ZoneOffset.UTC));
	}

	private void registrar(final UUID docente, final String valor) {
		casoUso.ejecutar(RegistrarNotaEstudianteDominio.crear(docente, ESTUDIANTE, NOTA, new BigDecimal(valor)));
	}

	private void esperarError(final TipoError tipo, final ClaveMensaje clave, final Runnable accion) {
		final NotaException error = assertThrows(NotaException.class, accion::run);
		assertEquals(tipo, error.getTipo());
		assertEquals(clave, error.getClaveMensajeUsuario());
		assertTrue(memoria.notasEstudiantes.isEmpty());
		assertTrue(servicios.notificaciones.isEmpty());
	}

	@Test
	@DisplayName("Registra la nota escalada a entero, con la fecha del registro")
	void registraNota() {
		registrar(DOCENTE, "4.35");

		assertEquals(1, memoria.notasEstudiantes.size());
		final NotaEstudianteEntidad registrada = memoria.notasEstudiantes.get(0);
		assertEquals(435, registrada.valor());
		assertEquals(NOTA, registrada.nota().id());
		assertEquals(DatosPrueba.INSCRIPCION, registrada.estudianteGrupo().id());
		assertEquals(AHORA, registrada.fechaRegistro());
		assertEquals(7, registrada.id().version());
	}

	@Test
	@DisplayName("Acepta los extremos 0 y 5")
	void aceptaExtremos() {
		registrar(DOCENTE, "0");
		memoria.notasEstudiantes.clear();
		registrar(DOCENTE, "5.00");

		assertEquals(500, memoria.notasEstudiantes.get(0).valor());
	}

	@Test
	@DisplayName("Rechaza la solicitud sin docente, estudiante o nota")
	void rechazaDatosFaltantes() {
		esperarError(TipoError.DATOS_INVALIDOS, ClaveMensaje.DOCENTE_OBLIGATORIO,
				() -> casoUso.ejecutar(RegistrarNotaEstudianteDominio.crear(null, ESTUDIANTE, NOTA, BigDecimal.ONE)));
		esperarError(TipoError.DATOS_INVALIDOS, ClaveMensaje.ESTUDIANTE_OBLIGATORIO,
				() -> casoUso.ejecutar(RegistrarNotaEstudianteDominio.crear(DOCENTE, null, NOTA, BigDecimal.ONE)));
		esperarError(TipoError.DATOS_INVALIDOS, ClaveMensaje.NOTA_OBLIGATORIA, () -> casoUso
				.ejecutar(RegistrarNotaEstudianteDominio.crear(DOCENTE, ESTUDIANTE, null, BigDecimal.ONE)));
		esperarError(TipoError.DATOS_INVALIDOS, ClaveMensaje.DOCENTE_OBLIGATORIO, () -> casoUso.ejecutar(null));
	}

	@Test
	@DisplayName("Rechaza la nota fuera del rango de 0 a 5 o sin valor")
	void rechazaFueraDeRango() {
		esperarError(TipoError.DATOS_INVALIDOS, ClaveMensaje.VALOR_FUERA_DE_RANGO, () -> registrar(DOCENTE, "5.01"));
		esperarError(TipoError.DATOS_INVALIDOS, ClaveMensaje.VALOR_FUERA_DE_RANGO, () -> registrar(DOCENTE, "-0.1"));
		esperarError(TipoError.DATOS_INVALIDOS, ClaveMensaje.VALOR_FUERA_DE_RANGO,
				() -> casoUso.ejecutar(RegistrarNotaEstudianteDominio.crear(DOCENTE, ESTUDIANTE, NOTA, null)));
	}

	@Test
	@DisplayName("Rechaza la nota con más de dos decimales")
	void rechazaTresDecimales() {
		esperarError(TipoError.DATOS_INVALIDOS, ClaveMensaje.VALOR_CON_MAS_DE_DOS_DECIMALES,
				() -> registrar(DOCENTE, "4.355"));
	}

	@Test
	@DisplayName("Rechaza al docente que no existe o no está activo")
	void rechazaDocente() {
		esperarError(TipoError.NO_ENCONTRADO, ClaveMensaje.DOCENTE_NO_EXISTE,
				() -> registrar(UUID.fromString("019a0000-0000-7000-8000-000000000999"), "4"));
		memoria.docentes.put(DOCENTE, DatosPrueba.docente(DOCENTE, "INACTIVO"));
		esperarError(TipoError.REGLA_NEGOCIO, ClaveMensaje.DOCENTE_INACTIVO, () -> registrar(DOCENTE, "4"));
	}

	@Test
	@DisplayName("Rechaza al estudiante que no existe o no está activo")
	void rechazaEstudiante() {
		memoria.estudiantes.clear();
		esperarError(TipoError.NO_ENCONTRADO, ClaveMensaje.ESTUDIANTE_NO_EXISTE, () -> registrar(DOCENTE, "4"));
		memoria.estudiantes.put(ESTUDIANTE, DatosPrueba.estudiante("INACTIVO"));
		esperarError(TipoError.REGLA_NEGOCIO, ClaveMensaje.ESTUDIANTE_INACTIVO, () -> registrar(DOCENTE, "4"));
	}

	@Test
	@DisplayName("Rechaza la nota que no existe")
	void rechazaNotaInexistente() {
		memoria.notas.clear();
		esperarError(TipoError.NO_ENCONTRADO, ClaveMensaje.NOTA_NO_EXISTE, () -> registrar(DOCENTE, "4"));
	}

	@Test
	@DisplayName("Rechaza cuando el grupo de la nota no existe o no está vigente")
	void rechazaGrupo() {
		memoria.grupos.clear();
		esperarError(TipoError.NO_ENCONTRADO, ClaveMensaje.GRUPO_NO_EXISTE, () -> registrar(DOCENTE, "4"));
		memoria.grupos.put(DatosPrueba.GRUPO, DatosPrueba.grupo(DOCENTE, "CERRADO"));
		esperarError(TipoError.REGLA_NEGOCIO, ClaveMensaje.GRUPO_NO_VIGENTE, () -> registrar(DOCENTE, "4"));
	}

	@Test
	@DisplayName("Rechaza al docente que no está asignado al grupo")
	void rechazaDocenteDeOtroGrupo() {
		esperarError(TipoError.REGLA_NEGOCIO, ClaveMensaje.DOCENTE_NO_ASIGNADO_AL_GRUPO,
				() -> registrar(OTRO_DOCENTE, "4"));
	}

	@Test
	@DisplayName("Rechaza al estudiante que no pertenece al grupo")
	void rechazaEstudianteDeOtroGrupo() {
		memoria.inscripciones.clear();
		esperarError(TipoError.REGLA_NEGOCIO, ClaveMensaje.ESTUDIANTE_NO_PERTENECE_AL_GRUPO,
				() -> registrar(DOCENTE, "4"));
	}

	@Test
	@DisplayName("Rechaza el registro antes de que abra y después de que cierre")
	void rechazaFueraDeFechas() {
		final GrupoEntidad grupo = memoria.grupos.get(DatosPrueba.GRUPO);
		memoria.notas.put(NOTA, DatosPrueba.nota(grupo, AHORA.plusSeconds(1), AHORA.plusSeconds(60)));
		esperarError(TipoError.REGLA_NEGOCIO, ClaveMensaje.REGISTRO_DE_NOTA_NO_HA_INICIADO,
				() -> registrar(DOCENTE, "4"));
		memoria.notas.put(NOTA, DatosPrueba.nota(grupo, AHORA.minusSeconds(60), AHORA));
		esperarError(TipoError.REGLA_NEGOCIO, ClaveMensaje.REGISTRO_DE_NOTA_FINALIZADO,
				() -> registrar(DOCENTE, "4"));
	}

	@Test
	@DisplayName("Permite registrar justo en el instante en que abre el registro")
	void registraAlAbrir() {
		final GrupoEntidad grupo = memoria.grupos.get(DatosPrueba.GRUPO);
		memoria.notas.put(NOTA, DatosPrueba.nota(grupo, AHORA, AHORA.plusSeconds(60)));

		registrar(DOCENTE, "3.5");

		assertEquals(350, memoria.notasEstudiantes.get(0).valor());
	}

	@Test
	@DisplayName("Rechaza registrar dos veces la misma nota al mismo estudiante")
	void rechazaDuplicado() {
		registrar(DOCENTE, "4");

		final NotaException error = assertThrows(NotaException.class, () -> registrar(DOCENTE, "3"));

		assertEquals(TipoError.CONFLICTO, error.getTipo());
		assertEquals(ClaveMensaje.NOTA_YA_REGISTRADA, error.getClaveMensajeUsuario());
		assertEquals(1, memoria.notasEstudiantes.size());
		assertEquals(400, memoria.notasEstudiantes.get(0).valor());
	}

	@Test
	@DisplayName("Exige todas sus dependencias al construirse")
	void exigeDependencias() {
		final Clock reloj = Clock.systemUTC();
		final ServiciosRegistroNota externos = new ServiciosRegistroNota(servicios.parametros, servicios.notificador);
		final NotaException error = assertThrows(NotaException.class,
				() -> new RegistrarNotaEstudianteCasoUsoImpl(null, externos, reloj));
		assertEquals(TipoError.INESPERADO, error.getTipo());
		final RepositoriosRegistroNota repositorios = new RepositoriosRegistroNota(memoria.docente,
				memoria.estudiante, memoria.nota, memoria.grupo, memoria.estudianteGrupo, memoria.notaEstudiante);
		assertThrows(NotaException.class, () -> new RegistrarNotaEstudianteCasoUsoImpl(repositorios, externos, null));
		assertThrows(NotaException.class, () -> new RegistrarNotaEstudianteCasoUsoImpl(repositorios, null, reloj));
		final ServiciosRegistroNota sinNotificador = new ServiciosRegistroNota(servicios.parametros, null);
		assertThrows(NotaException.class,
				() -> new RegistrarNotaEstudianteCasoUsoImpl(repositorios, sinNotificador, reloj));
	}

	@Test
	@DisplayName("Avisa por correo al estudiante con la plantilla de aprobado")
	void notificaAprobado() {
		registrar(DOCENTE, "4.3");

		assertEquals(1, servicios.notificaciones.size());
		final NotificacionCorreo aviso = servicios.notificaciones.get(0);
		assertEquals("ana.perez@example.com", aviso.destinatario());
		assertEquals("Ana Pérez", aviso.nombreDestinatario());
		assertEquals(ClaveMensaje.NOTIFICACION_NOTA_REGISTRADA_ASUNTO, aviso.plantillaAsunto());
		assertEquals(ClaveMensaje.NOTIFICACION_NOTA_REGISTRADA_APROBADA, aviso.plantillaCuerpo());
		assertEquals(List.of("Ana", "Parcial 1", "Ingeniería de Software 2", "4,30", "3,0"), aviso.parametros());
	}

	@Test
	@DisplayName("Usa la nota aprobatoria del catálogo de parámetros para elegir la plantilla")
	void notificaNoAprobadoSegunParametro() {
		servicios.valoresParametros.put(ClaveParametro.NOTA_VALOR_APROBATORIO, "3.5");

		registrar(DOCENTE, "3.4");

		final NotificacionCorreo aviso = servicios.notificaciones.get(0);
		assertEquals(ClaveMensaje.NOTIFICACION_NOTA_REGISTRADA_NO_APROBADA, aviso.plantillaCuerpo());
		assertEquals("3,5", aviso.parametros().get(4));
	}

	@Test
	@DisplayName("No envía aviso si el estudiante no tiene correo, pero sí registra la nota")
	void noNotificaSinCorreo() {
		memoria.estudiantes.put(ESTUDIANTE, new EstudianteEntidad(ESTUDIANTE, "1001", "Ana", "Pérez", "ACTIVO", " "));

		registrar(DOCENTE, "4");

		assertEquals(1, memoria.notasEstudiantes.size());
		assertTrue(servicios.notificaciones.isEmpty());
	}

	@Test
	@DisplayName("Valida el rango con los parámetros del catálogo")
	void validaRangoConParametros() {
		servicios.valoresParametros.put(ClaveParametro.NOTA_VALOR_MAXIMO, "4");
		esperarError(TipoError.DATOS_INVALIDOS, ClaveMensaje.VALOR_FUERA_DE_RANGO, () -> registrar(DOCENTE, "4.5"));

		servicios.valoresParametros.put(ClaveParametro.NOTA_VALOR_MAXIMO, "no es un número");
		registrar(DOCENTE, "4.5");

		assertEquals(450, memoria.notasEstudiantes.get(0).valor());
	}

}
