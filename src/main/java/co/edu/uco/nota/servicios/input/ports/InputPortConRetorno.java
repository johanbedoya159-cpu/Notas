package co.edu.uco.nota.servicios.input.ports;

public interface InputPortConRetorno<I, O> {

	O ejecutar(I solicitud);

}
