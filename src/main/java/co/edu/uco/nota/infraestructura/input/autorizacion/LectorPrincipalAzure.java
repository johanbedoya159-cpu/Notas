package co.edu.uco.nota.infraestructura.input.autorizacion;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.helper.TextoHelper;
import jakarta.servlet.http.HttpServletRequest;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Lee el usuario que la autenticación integrada de Azure Container Apps (Microsoft Entra ID) deja en los
 * encabezados de la solicitud. Azure elimina esos encabezados si vienen del cliente, así que solo llegan
 * cuando el usuario inició sesión.
 */
public final class LectorPrincipalAzure {

	public static final String ENCABEZADO_PRINCIPAL = "X-MS-CLIENT-PRINCIPAL";
	public static final String ENCABEZADO_NOMBRE = "X-MS-CLIENT-PRINCIPAL-NAME";

	private static final Logger LOGGER = LoggerFactory.getLogger(LectorPrincipalAzure.class);
	private static final List<String> TIPOS_ROL = List.of("roles",
			"http://schemas.microsoft.com/ws/2008/06/identity/claims/role");
	private static final List<String> TIPOS_CORREO = List.of("preferred_username", "email", "upn",
			"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress",
			"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/upn");
	private static final String TIPO_NOMBRE = "name";
	private static final String ROL_PROFESOR = "profesor";
	private static final String ROL_DOCENTE = "docente";
	private static final String ROL_ESTUDIANTE = "estudiante";

	private final JsonMapper json;

	public LectorPrincipalAzure(final JsonMapper json) {
		this.json = ObjetoHelper.requerirDependencia(json, "el lector de JSON");
	}

	public UsuarioSesion leer(final HttpServletRequest solicitud) {
		final String principal = TextoHelper.aplicarTrim(solicitud.getHeader(ENCABEZADO_PRINCIPAL));
		final String nombreEncabezado = TextoHelper.aplicarTrim(solicitud.getHeader(ENCABEZADO_NOMBRE));
		if (TextoHelper.estaVacio(principal)) {
			return new UsuarioSesion(nombreEncabezado, TextoHelper.VACIO, Rol.SIN_ROL);
		}
		try {
			final JsonNode raiz = json.readTree(Base64.getDecoder().decode(principal));
			final JsonNode reclamaciones = raiz.path("claims");
			final List<String> tiposRol = new ArrayList<>(TIPOS_ROL);
			tiposRol.add(TextoHelper.aplicarTrim(raiz.path("role_typ").asString()));
			final List<String> roles = new ArrayList<>();
			String correo = TextoHelper.VACIO;
			String nombre = TextoHelper.VACIO;
			for (int indice = 0; indice < reclamaciones.size(); indice++) {
				final JsonNode reclamacion = reclamaciones.get(indice);
				final String tipo = TextoHelper.aplicarTrim(reclamacion.path("typ").asString());
				final String valor = TextoHelper.aplicarTrim(reclamacion.path("val").asString());
				if (tiposRol.contains(tipo)) {
					roles.add(valor.toLowerCase(Locale.ROOT));
				} else if (TextoHelper.estaVacio(correo) && TIPOS_CORREO.contains(tipo)) {
					correo = valor;
				} else if (TIPO_NOMBRE.equals(tipo)) {
					nombre = valor;
				}
			}
			return new UsuarioSesion(TextoHelper.estaVacio(correo) ? nombreEncabezado : correo, nombre,
					rolDe(roles));
		} catch (final IllegalArgumentException | JacksonException excepcion) {
			LOGGER.warn("No se pudo leer el usuario de la sesión: {}", excepcion.getMessage());
			return new UsuarioSesion(nombreEncabezado, TextoHelper.VACIO, Rol.SIN_ROL);
		}
	}

	private static Rol rolDe(final List<String> roles) {
		if (roles.contains(ROL_PROFESOR) || roles.contains(ROL_DOCENTE)) {
			return Rol.PROFESOR;
		}
		return roles.contains(ROL_ESTUDIANTE) ? Rol.ESTUDIANTE : Rol.SIN_ROL;
	}

}
