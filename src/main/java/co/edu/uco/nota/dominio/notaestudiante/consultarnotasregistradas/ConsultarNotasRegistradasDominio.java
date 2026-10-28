package co.edu.uco.nota.dominio.notaestudiante.consultarnotasregistradas;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.crosscutting.paginacion.Paginacion;
import co.edu.uco.nota.dominio.comun.ConsultaPaginadaDominio;
import co.edu.uco.nota.dominio.notaestudiante.registrarnotaestudiante.NotaDominio;
import co.edu.uco.nota.servicios.output.ports.repositorios.NotaRepositorio;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEntidad;

public record ConsultarNotasRegistradasDominio(NotaDominio nota, ConsultaPaginadaDominio paginacion) {

	public ConsultarNotasRegistradasDominio {
		nota = ObjetoHelper.obtenerValorPorDefecto(nota, new NotaDominio(null));
		paginacion = ObjetoHelper.obtenerValorPorDefecto(paginacion,
				new ConsultaPaginadaDominio(Paginacion.PAGINA_INICIAL, Paginacion.TAMANO_POR_DEFECTO));
	}

	public NotaEntidad validar(final NotaRepositorio repositorio) {
		nota.validarDatoObligatorio();
		paginacion.validar();
		return nota.validarExiste(repositorio);
	}

}
