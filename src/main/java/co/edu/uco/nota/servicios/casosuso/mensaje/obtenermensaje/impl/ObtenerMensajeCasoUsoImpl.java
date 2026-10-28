package co.edu.uco.nota.servicios.casosuso.mensaje.obtenermensaje.impl;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.dominio.mensaje.obtenermensaje.MensajeDominio;
import co.edu.uco.nota.servicios.casosuso.mensaje.obtenermensaje.ObtenerMensajeCasoUso;
import co.edu.uco.nota.servicios.output.ports.serviciosexternos.catalogos.CatalogoMensajesServicio;

public final class ObtenerMensajeCasoUsoImpl implements ObtenerMensajeCasoUso {

	private final CatalogoMensajesServicio catalogo;

	public ObtenerMensajeCasoUsoImpl(final CatalogoMensajesServicio catalogo) {
		this.catalogo = ObjetoHelper.requerirDependencia(catalogo, "el catálogo de mensajes");
	}

	@Override
	public String ejecutar(final MensajeDominio datos) {
		return ObjetoHelper.obtenerValorPorDefecto(datos, new MensajeDominio(null, null)).construir(catalogo);
	}

}
