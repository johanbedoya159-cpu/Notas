package co.edu.uco.nota.servicios.casosuso.grupo.consultargrupos;

import co.edu.uco.nota.crosscutting.paginacion.Pagina;
import co.edu.uco.nota.dominio.grupo.consultargrupos.ConsultarGruposDominio;
import co.edu.uco.nota.servicios.casosuso.CasoUsoConRetorno;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;

public interface ConsultarGruposCasoUso extends CasoUsoConRetorno<Pagina<GrupoEntidad>, ConsultarGruposDominio> {

}
