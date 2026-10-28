package co.edu.uco.nota.servicios.casosuso.estudiante.consultarestudiantes;

import co.edu.uco.nota.crosscutting.paginacion.Pagina;
import co.edu.uco.nota.dominio.estudiante.consultarestudiantes.ConsultarEstudiantesDominio;
import co.edu.uco.nota.servicios.casosuso.CasoUsoConRetorno;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteEntidad;

public interface ConsultarEstudiantesCasoUso extends CasoUsoConRetorno<Pagina<EstudianteEntidad>, ConsultarEstudiantesDominio> {

}
