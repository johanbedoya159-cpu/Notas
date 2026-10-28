package co.edu.uco.nota.servicios.casosuso.grupo.consultarestudiantesgrupo;

import co.edu.uco.nota.crosscutting.paginacion.Pagina;
import co.edu.uco.nota.dominio.grupo.consultarestudiantesgrupo.ConsultarEstudiantesGrupoDominio;
import co.edu.uco.nota.servicios.casosuso.CasoUsoConRetorno;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteGrupoEntidad;

public interface ConsultarEstudiantesGrupoCasoUso extends CasoUsoConRetorno<Pagina<EstudianteGrupoEntidad>, ConsultarEstudiantesGrupoDominio> {

}
