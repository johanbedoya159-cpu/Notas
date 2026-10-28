package co.edu.uco.nota.infraestructura.output.repositorios.estructurados.mapper.impl;

import org.springframework.stereotype.Component;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades.GrupoEntidadJPA;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades.NotaEntidadJPA;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.mapper.EntidadMapper;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEntidad;

@Component
public class NotaEntidadMapper implements EntidadMapper<NotaEntidad, NotaEntidadJPA> {

	private final EntidadMapper<GrupoEntidad, GrupoEntidadJPA> grupoMapper;

	public NotaEntidadMapper(final EntidadMapper<GrupoEntidad, GrupoEntidadJPA> grupoMapper) {
		this.grupoMapper = grupoMapper;
	}

	@Override
	public NotaEntidad convertirAEntidad(final NotaEntidadJPA entidadJPA) {
		if (ObjetoHelper.esNulo(entidadJPA)) {
			return NotaEntidad.vacio();
		}
		return new NotaEntidad(entidadJPA.getId(), grupoMapper.convertirAEntidad(entidadJPA.getGrupo()),
				entidadJPA.getNombre(), entidadJPA.getFechaInicioRegistro(), entidadJPA.getFechaFinRegistro());
	}

	@Override
	public NotaEntidadJPA convertirAEntidadJPA(final NotaEntidad entidad) {
		final NotaEntidad origen = ObjetoHelper.obtenerValorPorDefecto(entidad, NotaEntidad.vacio());
		final NotaEntidadJPA entidadJPA = new NotaEntidadJPA();
		entidadJPA.setId(origen.id());
		entidadJPA.setGrupo(grupoMapper.convertirAEntidadJPA(origen.grupo()));
		entidadJPA.setNombre(origen.nombre());
		entidadJPA.setFechaInicioRegistro(origen.fechaInicioRegistro());
		entidadJPA.setFechaFinRegistro(origen.fechaFinRegistro());
		return entidadJPA;
	}

}
