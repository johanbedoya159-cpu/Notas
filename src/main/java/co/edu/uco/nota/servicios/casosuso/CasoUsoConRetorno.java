package co.edu.uco.nota.servicios.casosuso;

public interface CasoUsoConRetorno<O, I> {

	O ejecutar(I datos);

}
