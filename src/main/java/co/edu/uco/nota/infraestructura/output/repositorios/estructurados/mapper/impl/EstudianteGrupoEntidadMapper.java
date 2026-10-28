package co.edu.uco.nota.infraestructura.output.repositorios.estructurados.mapper.impl;

import org.springframework.stereotype.Component;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades.EstudianteEntidadJPA;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades.EstudianteGrupoEntidadJPA;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades.GrupoEntidadJPA;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.mapper.EntidadMapper;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteGrupoEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;

@Component
public class EstudianteGrupoEntidadMapper implements EntidadMapper<EstudianteGrupoEntidad, EstudianteGrupoEntidadJPA> {

	private final EntidadMapper<EstudianteEntidad, EstudianteEntidadJPA> estudianteMapper;
	private final EntidadMapper<GrupoEntidad, GrupoEntidadJPA> grupoMapper;

	public EstudianteGrupoEntidadMapper(final EntidadMapper<EstudianteEntidad, EstudianteEntidadJPA> estudianteMapper,
			final EntidadMapper<GrupoEntidad, GrupoEntidadJPA> grupoMapper) {
		this.estudianteMapper = estudianteMapper;
		this.grupoMapper = grupoMapper;
	}

	@Override
	public EstudianteGrupoEntidad convertirAEntidad(final EstudianteGrupoEntidadJPA entidadJPA) {
		if (ObjetoHelper.esNulo(entidadJPA)) {
			return EstudianteGrupoEntidad.vacio();
		}
		return new EstudianteGrupoEntidad(entidadJPA.getId(),
				estudianteMapper.convertirAEntidad(entidadJPA.getEstudiante()),
				grupoMapper.convertirAEntidad(entidadJPA.getGrupo()));
	}

	@Override
	public EstudianteGrupoEntidadJPA convertirAEntidadJPA(final EstudianteGrupoEntidad entidad) {
		final EstudianteGrupoEntidad origen = ObjetoHelper.obtenerValorPorDefecto(entidad,
				EstudianteGrupoEntidad.vacio());
		final EstudianteGrupoEntidadJPA entidadJPA = new EstudianteGrupoEntidadJPA();
		entidadJPA.setId(origen.id());
		entidadJPA.setEstudiante(estudianteMapper.convertirAEntidadJPA(origen.estudiante()));
		entidadJPA.setGrupo(grupoMapper.convertirAEntidadJPA(origen.grupo()));
		return entidadJPA;
	}

}
