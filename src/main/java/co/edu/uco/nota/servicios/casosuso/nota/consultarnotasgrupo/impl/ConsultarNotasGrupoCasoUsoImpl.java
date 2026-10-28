package co.edu.uco.nota.servicios.casosuso.nota.consultarnotasgrupo.impl;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.paginacion.Pagina;
import co.edu.uco.nota.dominio.nota.consultarnotasgrupo.ConsultarNotasGrupoDominio;
import co.edu.uco.nota.servicios.casosuso.nota.consultarnotasgrupo.ConsultarNotasGrupoCasoUso;
import co.edu.uco.nota.servicios.output.ports.repositorios.GrupoRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.NotaRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.criterios.NotaCriterio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEntidad;

public final class ConsultarNotasGrupoCasoUsoImpl implements ConsultarNotasGrupoCasoUso {

	private final GrupoRepositorio grupoRepositorio;
	private final NotaRepositorio notaRepositorio;

	public ConsultarNotasGrupoCasoUsoImpl(final GrupoRepositorio grupoRepositorio,
			final NotaRepositorio notaRepositorio) {
		this.grupoRepositorio = ObjetoHelper.requerirDependencia(grupoRepositorio, "el repositorio de grupos");
		this.notaRepositorio = ObjetoHelper.requerirDependencia(notaRepositorio, "el repositorio de notas");
	}

	@Override
	public Pagina<NotaEntidad> ejecutar(final ConsultarNotasGrupoDominio datos) {
		final ConsultarNotasGrupoDominio consulta = ObjetoHelper.obtenerValorPorDefecto(datos,
				new ConsultarNotasGrupoDominio(null, null));
		final GrupoEntidad grupo = consulta.validar(grupoRepositorio);
		return notaRepositorio.consultar(new NotaCriterio(grupo.id(), consulta.paginacion().aPaginacion()));
	}

}
