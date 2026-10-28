package co.edu.uco.nota.servicios.casosuso.nota.consultarnotasgrupo;

import co.edu.uco.nota.crosscutting.paginacion.Pagina;
import co.edu.uco.nota.dominio.nota.consultarnotasgrupo.ConsultarNotasGrupoDominio;
import co.edu.uco.nota.servicios.casosuso.CasoUsoConRetorno;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEntidad;

public interface ConsultarNotasGrupoCasoUso extends CasoUsoConRetorno<Pagina<NotaEntidad>, ConsultarNotasGrupoDominio> {

}
