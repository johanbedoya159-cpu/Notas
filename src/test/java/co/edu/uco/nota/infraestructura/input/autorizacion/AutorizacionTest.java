package co.edu.uco.nota.infraestructura.input.autorizacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import co.edu.uco.nota.crosscutting.paginacion.Pagina;
import co.edu.uco.nota.datosprueba.ServiciosExternosEnMemoria;
import co.edu.uco.nota.infraestructura.input.controladores.GrupoControlador;
import co.edu.uco.nota.infraestructura.input.controladores.NotaEstudianteControlador;
import co.edu.uco.nota.infraestructura.input.controladores.SesionControlador;
import co.edu.uco.nota.infraestructura.input.controladores.errores.ManejadorErrores;
import co.edu.uco.nota.infraestructura.input.controladores.respuesta.CatalogoMensajes;
import co.edu.uco.nota.servicios.casosuso.mensaje.obtenermensaje.impl.ObtenerMensajeCasoUsoImpl;
import co.edu.uco.nota.servicios.input.adapters.ConsultarEstudiantesGrupoInteractor;
import co.edu.uco.nota.servicios.input.adapters.ConsultarGruposInteractor;
import co.edu.uco.nota.servicios.input.adapters.ConsultarNotasGrupoInteractor;
import co.edu.uco.nota.servicios.input.adapters.ObtenerMensajeInteractor;
import co.edu.uco.nota.servicios.input.dto.response.MiGrupoResponseDTO;
import tools.jackson.databind.json.JsonMapper;

class AutorizacionTest {

	private static final String TIPO_ROL = "http://schemas.microsoft.com/ws/2008/06/identity/claims/role";

	private final LectorPrincipalAzure lector = new LectorPrincipalAzure(JsonMapper.builder().build());
	private final List<String> correosConsultados = new ArrayList<>();

	@BeforeEach
	void limpiar() {
		correosConsultados.clear();
	}

	private MockMvc mvc(final boolean habilitada) {
		final CatalogoMensajes catalogo = new CatalogoMensajes(new ObtenerMensajeInteractor(
				new ObtenerMensajeCasoUsoImpl(ServiciosExternosEnMemoria.catalogoLocal())));
		final SesionControlador sesion = new SesionControlador(solicitud -> {
			correosConsultados.add(solicitud.correo());
			return List.of(new MiGrupoResponseDTO(null, "IS2-01", "Ingeniería de Software 2", "Carlos Mesa",
					"VIGENTE", List.of()));
		});
		final GrupoControlador grupos = new GrupoControlador(
				new ConsultarGruposInteractor(consulta -> Pagina.vacia(consulta.paginacion().aPaginacion())),
				new ConsultarEstudiantesGrupoInteractor(consulta -> Pagina.vacia(consulta.paginacion().aPaginacion())),
				new ConsultarNotasGrupoInteractor(consulta -> Pagina.vacia(consulta.paginacion().aPaginacion())));
		final NotaEstudianteControlador registro = new NotaEstudianteControlador(solicitud -> {
		}, catalogo);
		return MockMvcBuilders.standaloneSetup(sesion, grupos, registro)
				.setControllerAdvice(new ManejadorErrores(catalogo))
				.addInterceptors(new AutorizacionInterceptor(habilitada, lector)).build();
	}

	private static String principal(final String tipoRol, final String... reclamaciones) {
		final StringBuilder json = new StringBuilder("{\"auth_typ\":\"aad\",\"role_typ\":\"" + tipoRol
				+ "\",\"claims\":[");
		for (int indice = 0; indice < reclamaciones.length; indice += 2) {
			if (indice > 0) {
				json.append(',');
			}
			json.append("{\"typ\":\"").append(reclamaciones[indice]).append("\",\"val\":\"")
					.append(reclamaciones[indice + 1]).append("\"}");
		}
		return Base64.getEncoder().encodeToString(json.append("]}").toString().getBytes(StandardCharsets.UTF_8));
	}

	private static String profesor() {
		return principal("roles", "name", "Carlos Mesa", "preferred_username", "carlos@uco.net.co", "roles",
				"Profesor");
	}

	private static String estudiante() {
		return principal(TIPO_ROL, "name", "Ana Pérez", "email", "ana@uco.net.co", TIPO_ROL, "Estudiante");
	}

	@Test
	@DisplayName("El profesor puede consultar grupos y registrar notas")
	void profesorHaceTodo() throws Exception {
		final MockMvc mvc = mvc(true);
		mvc.perform(get("/api/v1/grupos").header(LectorPrincipalAzure.ENCABEZADO_PRINCIPAL, profesor()))
				.andExpect(status().isOk());
		mvc.perform(post("/api/v1/notas-estudiantes").header(LectorPrincipalAzure.ENCABEZADO_PRINCIPAL, profesor())
				.contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isCreated());
		mvc.perform(get("/api/v1/sesion").header(LectorPrincipalAzure.ENCABEZADO_PRINCIPAL, profesor()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.datos.rol").value("PROFESOR"))
				.andExpect(jsonPath("$.datos.correo").value("carlos@uco.net.co"))
				.andExpect(jsonPath("$.datos.nombre").value("Carlos Mesa"));
	}

	@Test
	@DisplayName("El estudiante solo ve sus notas y no puede consultar grupos ni registrar")
	void estudianteSoloVeSusNotas() throws Exception {
		final MockMvc mvc = mvc(true);
		mvc.perform(get("/api/v1/mis-notas").header(LectorPrincipalAzure.ENCABEZADO_PRINCIPAL, estudiante()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.datos[0].codigo").value("IS2-01"));
		assertEquals(List.of("ana@uco.net.co"), correosConsultados);

		mvc.perform(get("/api/v1/grupos").header(LectorPrincipalAzure.ENCABEZADO_PRINCIPAL, estudiante()))
				.andExpect(status().isForbidden()).andExpect(jsonPath("$.exito").value(false))
				.andExpect(jsonPath("$.mensajes[0]").value("No tiene permiso para hacer esta operación con su rol."));
		mvc.perform(post("/api/v1/notas-estudiantes").header(LectorPrincipalAzure.ENCABEZADO_PRINCIPAL, estudiante())
				.contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isForbidden());
		mvc.perform(get("/api/v1/sesion").header(LectorPrincipalAzure.ENCABEZADO_PRINCIPAL, estudiante()))
				.andExpect(jsonPath("$.datos.rol").value("ESTUDIANTE"));
	}

	@Test
	@DisplayName("Sin rol solo se puede consultar la sesión")
	void sinRol() throws Exception {
		final MockMvc mvc = mvc(true);
		mvc.perform(get("/api/v1/sesion").header(LectorPrincipalAzure.ENCABEZADO_NOMBRE, "x@uco.net.co"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.datos.rol").value("SIN_ROL"))
				.andExpect(jsonPath("$.datos.correo").value("x@uco.net.co"));
		mvc.perform(get("/api/v1/mis-notas")).andExpect(status().isForbidden());
		mvc.perform(get("/api/v1/grupos")).andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("Con la autorización apagada todos entran como profesor, como antes")
	void autorizacionApagada() throws Exception {
		final MockMvc mvc = mvc(false);
		mvc.perform(get("/api/v1/grupos")).andExpect(status().isOk());
		mvc.perform(get("/api/v1/sesion").header(LectorPrincipalAzure.ENCABEZADO_PRINCIPAL, estudiante()))
				.andExpect(jsonPath("$.datos.rol").value("PROFESOR"));
		assertFalse(new AutorizacionInterceptor(false, lector).estaHabilitada());
		assertTrue(new AutorizacionInterceptor(true, lector).estaHabilitada());
	}

	@Test
	@DisplayName("El lector tolera encabezados dañados y usa el nombre del encabezado si falta el correo")
	void lectorTolerante() {
		final MockHttpServletRequest danado = new MockHttpServletRequest();
		danado.addHeader(LectorPrincipalAzure.ENCABEZADO_PRINCIPAL, "esto-no-es-base64!");
		danado.addHeader(LectorPrincipalAzure.ENCABEZADO_NOMBRE, "ana@uco.net.co");
		assertEquals(new UsuarioSesion("ana@uco.net.co", "", Rol.SIN_ROL), lector.leer(danado));

		final MockHttpServletRequest sinCorreo = new MockHttpServletRequest();
		sinCorreo.addHeader(LectorPrincipalAzure.ENCABEZADO_PRINCIPAL, principal("roles", "roles", "Docente"));
		sinCorreo.addHeader(LectorPrincipalAzure.ENCABEZADO_NOMBRE, "carlos@uco.net.co");
		assertEquals(new UsuarioSesion("carlos@uco.net.co", "", Rol.PROFESOR), lector.leer(sinCorreo));

		final MockHttpServletRequest otroRol = new MockHttpServletRequest();
		otroRol.addHeader(LectorPrincipalAzure.ENCABEZADO_PRINCIPAL,
				principal("roles", "upn", "z@uco.net.co", "roles", "Invitado"));
		assertEquals(Rol.SIN_ROL, lector.leer(otroRol).rol());

		assertEquals(Rol.SIN_ROL, new UsuarioSesion(null, null, null).rol());
		assertEquals(UsuarioSesion.anonimo(), new UsuarioSesion("", "", Rol.SIN_ROL));
	}

	@Test
	@DisplayName("Cada rol tiene sus rutas permitidas")
	void reglasPorRol() {
		assertTrue(Rol.PROFESOR.puedeAcceder("POST", "/api/v1/notas-estudiantes"));
		assertTrue(Rol.ESTUDIANTE.puedeAcceder("GET", Rol.RUTA_MIS_NOTAS));
		assertFalse(Rol.ESTUDIANTE.puedeAcceder("POST", Rol.RUTA_MIS_NOTAS));
		assertFalse(Rol.ESTUDIANTE.puedeAcceder("GET", "/api/v1/estudiantes"));
		assertTrue(Rol.SIN_ROL.puedeAcceder("GET", Rol.RUTA_SESION));
		assertFalse(Rol.SIN_ROL.puedeAcceder("GET", Rol.RUTA_MIS_NOTAS));
	}

}
