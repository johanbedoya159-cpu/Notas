package co.edu.uco.nota.infraestructura.output.repositorios.estructurados.mapper.impl;

import org.springframework.stereotype.Component;

import co.edu.uco.nota.crosscutting.helper.ObjetoHelper;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades.DocenteEntidadJPA;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades.GrupoEntidadJPA;
import co.edu.uco.nota.infraestructura.output.repositorios.estructurados.mapper.EntidadMapper;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.DocenteEntidad;
import co.edu.uco.nota.servicios.output.ports.repositorios.entidades.GrupoEntidad;

@Component
public class GrupoEntidadMapper implements EntidadMapper<GrupoEntidad, GrupoEntidadJPA> {

	private final EntidadMapper<DocenteEntidad, DocenteEntidadJPA> docenteMapper;

	public GrupoEntidadMapper(final EntidadMapper<DocenteEntidad, DocenteEntidadJPA> docenteMapper) {
		this.docenteMapper = docenteMapper;
	}

	@Override
	public GrupoEntidad convertirAEntidad(final GrupoEntidadJPA entidadJPA) {
		if (ObjetoHelper.esNulo(entidadJPA)) {
			return GrupoEntidad.vacio();
		}
		return new GrupoEntidad(entidadJPA.getId(), entidadJPA.getCodigo(), entidadJPA.getNombre(),
				docenteMapper.convertirAEntidad(entidadJPA.getDocente()), entidadJPA.getEstado());
	}

	@Override
	public GrupoEntidadJPA convertirAEntidadJPA(final GrupoEntidad entidad) {
		final GrupoEntidad origen = ObjetoHelper.obtenerValorPorDefecto(entidad, GrupoEntidad.vacio());
		final GrupoEntidadJPA entidadJPA = new GrupoEntidadJPA();
		entidadJPA.setId(origen.id());
		entidadJPA.setCodigo(origen.codigo());
		entidadJPA.setNombre(origen.nombre());
		entidadJPA.setDocente(docenteMapper.convertirAEntidadJPA(origen.docente()));
		entidadJPA.setEstado(origen.estado());
		return entidadJPA;
	}

}
