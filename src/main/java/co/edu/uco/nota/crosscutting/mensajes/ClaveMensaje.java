package co.edu.uco.nota.crosscutting.mensajes;

public enum ClaveMensaje {

	DOCENTE_OBLIGATORIO("notaestudiante.docente.obligatorio"),
	ESTUDIANTE_OBLIGATORIO("notaestudiante.estudiante.obligatorio"),
	NOTA_OBLIGATORIA("notaestudiante.nota.obligatoria"),
	GRUPO_OBLIGATORIO("grupo.obligatorio"),
	VALOR_FUERA_DE_RANGO("notaestudiante.valor.rango"),
	VALOR_CON_MAS_DE_DOS_DECIMALES("notaestudiante.valor.decimales"),
	DOCENTE_NO_EXISTE("docente.no-existe"),
	DOCENTE_INACTIVO("docente.inactivo"),
	ESTUDIANTE_NO_EXISTE("estudiante.no-existe"),
	ESTUDIANTE_INACTIVO("estudiante.inactivo"),
	NOTA_NO_EXISTE("nota.no-existe"),
	GRUPO_NO_EXISTE("grupo.no-existe"),
	GRUPO_NO_VIGENTE("grupo.no-vigente"),
	DOCENTE_NO_ASIGNADO_AL_GRUPO("grupo.docente.no-asignado"),
	ESTUDIANTE_NO_PERTENECE_AL_GRUPO("grupo.estudiante.no-pertenece"),
	REGISTRO_DE_NOTA_NO_HA_INICIADO("nota.registro.no-iniciado"),
	REGISTRO_DE_NOTA_FINALIZADO("nota.registro.finalizado"),
	NOTA_YA_REGISTRADA("notaestudiante.ya-registrada"),
	NOTA_REGISTRADA("notaestudiante.registrada"),
	PAGINA_INVALIDA("consulta.pagina.invalida"),
	TAMANO_DE_PAGINA_INVALIDO("consulta.tamano.invalido"),
	TEXTO_DE_BUSQUEDA_MUY_LARGO("consulta.texto.muy-largo"),
	SOLICITUD_INVALIDA("solicitud.invalida"),
	PARAMETRO_INVALIDO("solicitud.parametro.invalido"),
	RECURSO_NO_ENCONTRADO("solicitud.recurso.no-encontrado"),
	METODO_NO_PERMITIDO("solicitud.metodo.no-permitido"),
	ERROR_INESPERADO("error.inesperado"),
	ACCESO_DENEGADO("solicitud.acceso.denegado"),
	CORREO_OBLIGATORIO("estudiante.correo.obligatorio"),
	ESTUDIANTE_SIN_REGISTRO("estudiante.correo.no-registrado"),
	NOTIFICACION_NOTA_REGISTRADA_ASUNTO("notificacion.nota-registrada.asunto"),
	NOTIFICACION_NOTA_REGISTRADA_APROBADA("notificacion.nota-registrada.cuerpo.aprobada"),
	NOTIFICACION_NOTA_REGISTRADA_NO_APROBADA("notificacion.nota-registrada.cuerpo.no-aprobada");

	private final String clave;

	ClaveMensaje(final String clave) {
		this.clave = clave;
	}

	public String getClave() {
		return clave;
	}

}
