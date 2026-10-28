package co.edu.uco.nota.infraestructura.output.repositorios.estructurados.mapper;

public interface EntidadMapper<E, J> {

	E convertirAEntidad(J entidadJPA);

	J convertirAEntidadJPA(E entidad);

}
