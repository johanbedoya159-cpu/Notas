package co.edu.uco.nota.servicios.casosuso.notaestudiante.consultarnotasregistradas;

import co.edu.uco.nota.crosscutting.paginacion.Pagina;
import co.edu.uco.nota.dominio.notaestudiante.consultarnotasregistradas.ConsultarNotasRegistradasDominio;
import co.edu.uco.nota.servicios.casosuso.CasoUsoConRetorno;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEstudianteEntidad;

public interface ConsultarNotasRegistradasCasoUso extends CasoUsoConRetorno<Pagina<NotaEstudianteEntidad>, ConsultarNotasRegistradasDominio> {

}
