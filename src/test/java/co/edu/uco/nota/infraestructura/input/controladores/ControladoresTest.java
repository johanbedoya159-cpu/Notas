package co.edu.uco.nota.infraestructura.input.controladores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import co.edu.uco.nota.crosscutting.excepcion.NotaException;
import co.edu.uco.nota.crosscutting.excepcion.TipoError;
import co.edu.uco.nota.crosscutting.mensajes.ClaveMensaje;
import co.edu.uco.nota.crosscutting.paginacion.Pagina;
import co.edu.uco.nota.datosprueba.DatosPrueba;
import co.edu.uco.nota.infraestructura.input.controladores.errores.ManejadorErrores;
import co.edu.uco.nota.infraestructura.input.controladores.respuesta.CatalogoMensajes;
import co.edu.uco.nota.infraestructura.input.filtros.TrazabilidadFiltro;
import co.edu.uco.nota.servicios.input.adapters.ConsultarEstudiantesGrupoInteractor;
import co.edu.uco.nota.servicios.input.adapters.ConsultarEstudiantesInteractor;
import co.edu.uco.nota.servicios.input.adapters.ConsultarGruposInteractor;
import co.edu.uco.nota.servicios.input.adapters.ConsultarNotasGrupoInteractor;
import co.edu.uco.nota.servicios.input.adapters.ConsultarNotasRegistradasInteractor;
import co.edu.uco.nota.servicios.input.dto.request.RegistrarNotaEstudianteRequestDTO;

class ControladoresTest {

	private final List<RegistrarNotaEstudianteRequestDTO> registradas = new ArrayList<>();
	private RuntimeException fallaAlRegistrar;
	private MockMvc mvc;

	@BeforeEach
	void preparar() {
		final ResourceBundleMessageSource fuente = new ResourceBundleMessageSource();
		fuente.setBasename("mensajes/mensajes");
		fuente.setDefaultEncoding("UTF-8");
		fuente.setFallbackToSystemLocale(false);
		final CatalogoMensajes catalogo = new CatalogoMensajes(fuente);

		final NotaEstudianteControlador notaEstudiante = new NotaEstudianteControlador(solicitud -> {
			if (fallaAlRegistrar != null) {
				throw fallaAlRegistrar;
			}
			registradas.add(solicitud);
		}, catalogo);
		final EstudianteControlador estudiantes = new EstudianteControlador(new ConsultarEstudiantesInteractor(
				consulta -> new Pagina<>(List.of(DatosPrueba.estudiante("ACTIVO")), 0, 20, 1)));
		final GrupoControlador grupos = new GrupoControlador(
				new ConsultarGruposInteractor(consulta -> Pagina.vacia(consulta.paginacion().aPaginacion())),
				new ConsultarEstudiantesGrupoInteractor(consulta -> Pagina.vacia(consulta.paginacion().aPaginacion())),
				new ConsultarNotasGrupoInteractor(consulta -> Pagina.vacia(consulta.paginacion().aPaginacion())));
		final NotaControlador notas = new NotaControlador(new ConsultarNotasRegistradasInteractor(
				consulta -> Pagina.vacia(consulta.paginacion().aPaginacion())));

		mvc = MockMvcBuilders.standaloneSetup(notaEstudiante, estudiantes, grupos, notas)
				.setControllerAdvice(new ManejadorErrores(catalogo)).addFilters(new TrazabilidadFiltro()).build();
	}

	@Test
	@DisplayName("Registrar responde 201 con el mensaje de éxito y la trazabilidad")
	void registraNota() throws Exception {
		mvc.perform(post("/api/v1/notas-estudiantes").contentType(MediaType.APPLICATION_JSON).content("""
				{"docente":"019a0000-0000-7000-8000-000000000101","estudiante":"019a0000-0000-7000-8000-000000000201",
				 "nota":"019a0000-0000-7000-8000-000000000501","valor":4.35}"""))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.exito").value(true))
				.andExpect(jsonPath("$.mensajes[0]").value("La nota se registró correctamente."))
				.andExpect(header().exists(TrazabilidadFiltro.ENCABEZADO))
				.andExpect(jsonPath("$.trazabilidad").isNotEmpty());

		assertEquals(DatosPrueba.NOTA, registradas.get(0).nota());
	}

	@Test
	@DisplayName("Una regla de negocio incumplida responde 422 con el mensaje del catálogo")
	void reglaDeNegocio() throws Exception {
		fallaAlRegistrar = NotaException.crear(TipoError.REGLA_NEGOCIO, ClaveMensaje.ESTUDIANTE_NO_PERTENECE_AL_GRUPO,
				"técnico");

		mvc.perform(post("/api/v1/notas-estudiantes").contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isUnprocessableContent()).andExpect(jsonPath("$.exito").value(false))
				.andExpect(jsonPath("$.mensajes[0]").value("El estudiante no pertenece a este grupo."));
	}

	@Test
	@DisplayName("Cada tipo de error responde con su código y sin detalles técnicos")
	void codigosDeError() throws Exception {
		fallaAlRegistrar = NotaException.crear(TipoError.CONFLICTO, ClaveMensaje.NOTA_YA_REGISTRADA, "duplicada");
		mvc.perform(post("/api/v1/notas-estudiantes").contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isConflict());

		fallaAlRegistrar = NotaException.crear(TipoError.NO_ENCONTRADO, ClaveMensaje.DOCENTE_NO_EXISTE, "no está");
		mvc.perform(post("/api/v1/notas-estudiantes").contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isNotFound());

		fallaAlRegistrar = new IllegalStateException("se cayó la base");
		mvc.perform(post("/api/v1/notas-estudiantes").contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.mensajes[0]").value("Ocurrió un problema inesperado. Intente de nuevo más tarde."));

		fallaAlRegistrar = NotaException.crear(TipoError.INESPERADO, ClaveMensaje.ERROR_INESPERADO, "sin reloj");
		mvc.perform(post("/api/v1/notas-estudiantes").contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	@DisplayName("Un cuerpo que no es JSON válido responde 400")
	void jsonInvalido() throws Exception {
		mvc.perform(post("/api/v1/notas-estudiantes").contentType(MediaType.APPLICATION_JSON).content("{nota:"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensajes[0]")
						.value("La solicitud no tiene el formato esperado. Revise los datos enviados."));
	}

	@Test
	@DisplayName("Consulta estudiantes, grupos y notas con respuesta paginada")
	void consultas() throws Exception {
		mvc.perform(get("/api/v1/estudiantes").param("texto", "ana")).andExpect(status().isOk())
				.andExpect(jsonPath("$.datos.contenido[0].nombreCompleto").value("Ana Pérez"))
				.andExpect(jsonPath("$.datos.totalElementos").value(1));
		mvc.perform(get("/api/v1/grupos").param("tamano", "5")).andExpect(status().isOk())
				.andExpect(jsonPath("$.datos.tamano").value(5));
		mvc.perform(get("/api/v1/grupos/{grupo}/estudiantes", DatosPrueba.GRUPO)).andExpect(status().isOk());
		mvc.perform(get("/api/v1/grupos/{grupo}/notas", DatosPrueba.GRUPO)).andExpect(status().isOk());
		mvc.perform(get("/api/v1/notas/{nota}/estudiantes", DatosPrueba.NOTA)).andExpect(status().isOk());
	}

	@Test
	@DisplayName("Un identificador mal escrito en la dirección responde 400")
	void parametroInvalido() throws Exception {
		mvc.perform(get("/api/v1/grupos").param("docente", "no-es-uuid")).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensajes[0]")
						.value("Uno de los parámetros de la consulta no tiene un valor válido."));
	}

	@Test
	@DisplayName("Un método no permitido responde 405 y reutiliza la trazabilidad recibida")
	void metodoNoPermitido() throws Exception {
		final String trazabilidad = "019a0000-0000-7000-8000-00000000abcd";
		mvc.perform(get("/api/v1/notas-estudiantes").header(TrazabilidadFiltro.ENCABEZADO, trazabilidad))
				.andExpect(status().isMethodNotAllowed())
				.andExpect(header().string(TrazabilidadFiltro.ENCABEZADO, trazabilidad));
	}

}
