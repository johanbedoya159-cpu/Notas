package co.edu.uco.nota.infraestructura.output.serviciosexternos.azure;

import co.edu.uco.nota.crosscutting.helper.TextoHelper;

public record CredencialesAzure(String endpointIdentidad, String encabezadoIdentidad, String autoridad,
		String inquilino, String cliente, String secretoCliente) {

	public static final String AUTORIDAD_POR_DEFECTO = "https://login.microsoftonline.com";

	public CredencialesAzure {
		endpointIdentidad = TextoHelper.aplicarTrim(endpointIdentidad);
		encabezadoIdentidad = TextoHelper.aplicarTrim(encabezadoIdentidad);
		autoridad = TextoHelper.estaVacio(autoridad) ? AUTORIDAD_POR_DEFECTO : TextoHelper.aplicarTrim(autoridad);
		autoridad = autoridad.endsWith("/") ? autoridad.substring(0, autoridad.length() - 1) : autoridad;
		inquilino = TextoHelper.aplicarTrim(inquilino);
		cliente = TextoHelper.aplicarTrim(cliente);
		secretoCliente = TextoHelper.aplicarTrim(secretoCliente);
	}

	public static CredencialesAzure administrada(final String endpoint, final String encabezado) {
		return new CredencialesAzure(endpoint, encabezado, null, null, null, null);
	}

	public static CredencialesAzure aplicacion(final String autoridad, final String inquilino, final String cliente,
			final String secretoCliente) {
		return new CredencialesAzure(null, null, autoridad, inquilino, cliente, secretoCliente);
	}

	public boolean usaIdentidadAdministrada() {
		return !endpointIdentidad.isEmpty() && !encabezadoIdentidad.isEmpty();
	}

	public boolean usaAplicacion() {
		return !inquilino.isEmpty() && !cliente.isEmpty() && !secretoCliente.isEmpty();
	}

}
