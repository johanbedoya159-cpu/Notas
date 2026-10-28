package co.edu.uco.nota.infraestructura.output.repositorios.estructurados.mapper.impl;

import org.springframework.stereotype.Component;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades.EstudianteEntidadJPA;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.mapper.EntidadMapper;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteEntidad;

@Component
public class EstudianteEntidadMapper implements EntidadMapper<EstudianteEntidad, EstudianteEntidadJPA> {

	@Override
	public EstudianteEntidad convertirAEntidad(final EstudianteEntidadJPA entidadJPA) {
		if (ObjetoHelper.esNulo(entidadJPA)) {
			return EstudianteEntidad.vacio();
		}
		return new EstudianteEntidad(entidadJPA.getId(), entidadJPA.getIdentificacion(), entidadJPA.getNombres(),
				entidadJPA.getApellidos(), entidadJPA.getEstado(), entidadJPA.getCorreo());
	}

	@Override
	public EstudianteEntidadJPA convertirAEntidadJPA(final EstudianteEntidad entidad) {
		final EstudianteEntidad origen = ObjetoHelper.obtenerValorPorDefecto(entidad, EstudianteEntidad.vacio());
		final EstudianteEntidadJPA entidadJPA = new EstudianteEntidadJPA();
		entidadJPA.setId(origen.id());
		entidadJPA.setIdentificacion(origen.identificacion());
		entidadJPA.setNombres(origen.nombres());
		entidadJPA.setApellidos(origen.apellidos());
		entidadJPA.setEstado(origen.estado());
		entidadJPA.setCorreo(origen.correo());
		return entidadJPA;
	}

}
