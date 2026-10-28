package co.edu.uco.nota.infraestructura.output.repositorios.estructurados.mapper.impl;

import org.springframework.stereotype.Component;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades.EstudianteGrupoEntidadJPA;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades.NotaEntidadJPA;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades.NotaEstudianteEntidadJPA;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.mapper.EntidadMapper;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.EstudianteGrupoEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.NotaEstudianteEntidad;

@Component
public class NotaEstudianteEntidadMapper implements EntidadMapper<NotaEstudianteEntidad, NotaEstudianteEntidadJPA> {

	private final EntidadMapper<EstudianteGrupoEntidad, EstudianteGrupoEntidadJPA> estudianteGrupoMapper;
	private final EntidadMapper<NotaEntidad, NotaEntidadJPA> notaMapper;

	public NotaEstudianteEntidadMapper(
			final EntidadMapper<EstudianteGrupoEntidad, EstudianteGrupoEntidadJPA> estudianteGrupoMapper,
			final EntidadMapper<NotaEntidad, NotaEntidadJPA> notaMapper) {
		this.estudianteGrupoMapper = estudianteGrupoMapper;
		this.notaMapper = notaMapper;
	}

	@Override
	public NotaEstudianteEntidad convertirAEntidad(final NotaEstudianteEntidadJPA entidadJPA) {
		if (ObjetoHelper.esNulo(entidadJPA)) {
			return new NotaEstudianteEntidad(null, null, null, 0, null);
		}
		return new NotaEstudianteEntidad(entidadJPA.getId(),
				estudianteGrupoMapper.convertirAEntidad(entidadJPA.getEstudianteGrupo()),
				notaMapper.convertirAEntidad(entidadJPA.getNota()), entidadJPA.getValor(),
				entidadJPA.getFechaRegistro());
	}

	@Override
	public NotaEstudianteEntidadJPA convertirAEntidadJPA(final NotaEstudianteEntidad entidad) {
		final NotaEstudianteEntidad origen = ObjetoHelper.obtenerValorPorDefecto(entidad,
				new NotaEstudianteEntidad(null, null, null, 0, null));
		final NotaEstudianteEntidadJPA entidadJPA = new NotaEstudianteEntidadJPA();
		entidadJPA.setId(origen.id());
		entidadJPA.setEstudianteGrupo(estudianteGrupoMapper.convertirAEntidadJPA(origen.estudianteGrupo()));
		entidadJPA.setNota(notaMapper.convertirAEntidadJPA(origen.nota()));
		entidadJPA.setValor(origen.valor());
		entidadJPA.setFechaRegistro(origen.fechaRegistro());
		return entidadJPA;
	}

}
