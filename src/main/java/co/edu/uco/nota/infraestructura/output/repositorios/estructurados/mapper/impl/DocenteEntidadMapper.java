package co.edu.uco.nota.infraestructura.output.repositorios.estructurados.mapper.impl;

import org.springframework.stereotype.Component;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades.DocenteEntidadJPA;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.mapper.EntidadMapper;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.DocenteEntidad;

@Component
public class DocenteEntidadMapper implements EntidadMapper<DocenteEntidad, DocenteEntidadJPA> {

	@Override
	public DocenteEntidad convertirAEntidad(final DocenteEntidadJPA entidadJPA) {
		if (ObjetoHelper.esNulo(entidadJPA)) {
			return DocenteEntidad.vacio();
		}
		return new DocenteEntidad(entidadJPA.getId(), entidadJPA.getIdentificacion(), entidadJPA.getNombre(),
				entidadJPA.getEstado());
	}

	@Override
	public DocenteEntidadJPA convertirAEntidadJPA(final DocenteEntidad entidad) {
		final DocenteEntidad origen = ObjetoHelper.obtenerValorPorDefecto(entidad, DocenteEntidad.vacio());
		final DocenteEntidadJPA entidadJPA = new DocenteEntidadJPA();
		entidadJPA.setId(origen.id());
		entidadJPA.setIdentificacion(origen.identificacion());
		entidadJPA.setNombre(origen.nombre());
		entidadJPA.setEstado(origen.estado());
		return entidadJPA;
	}

}
