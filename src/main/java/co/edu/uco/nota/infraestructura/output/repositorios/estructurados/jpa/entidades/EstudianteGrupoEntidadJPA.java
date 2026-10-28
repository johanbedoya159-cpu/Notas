package co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "estudiante_grupo")
public class EstudianteGrupoEntidadJPA {

	@Id
	@Column(name = "id")
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "id_estudiante")
	private EstudianteEntidadJPA estudiante;

	@ManyToOne
	@JoinColumn(name = "id_grupo")
	private GrupoEntidadJPA grupo;

	public UUID getId() {
		return id;
	}

	public void setId(final UUID id) {
		this.id = id;
	}

	public EstudianteEntidadJPA getEstudiante() {
		return estudiante;
	}

	public void setEstudiante(final EstudianteEntidadJPA estudiante) {
		this.estudiante = estudiante;
	}

	public GrupoEntidadJPA getGrupo() {
		return grupo;
	}

	public void setGrupo(final GrupoEntidadJPA grupo) {
		this.grupo = grupo;
	}

}
