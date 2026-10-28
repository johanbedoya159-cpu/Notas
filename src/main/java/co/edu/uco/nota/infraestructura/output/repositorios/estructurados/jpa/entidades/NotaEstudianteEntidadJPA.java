package co.edu.uco.nota.infraestructura.output.repositorios.estructurados.jpa.entidades;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "nota_estudiante")
public class NotaEstudianteEntidadJPA {

	@Id
	@Column(name = "id")
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "id_estudiante_grupo")
	private EstudianteGrupoEntidadJPA estudianteGrupo;

	@ManyToOne
	@JoinColumn(name = "id_nota")
	private NotaEntidadJPA nota;

	@Column(name = "valor")
	private int valor;

	@Column(name = "fecha_registro")
	private Instant fechaRegistro;

	public UUID getId() {
		return id;
	}

	public void setId(final UUID id) {
		this.id = id;
	}

	public EstudianteGrupoEntidadJPA getEstudianteGrupo() {
		return estudianteGrupo;
	}

	public void setEstudianteGrupo(final EstudianteGrupoEntidadJPA estudianteGrupo) {
		this.estudianteGrupo = estudianteGrupo;
	}

	public NotaEntidadJPA getNota() {
		return nota;
	}

	public void setNota(final NotaEntidadJPA nota) {
		this.nota = nota;
	}

	public int getValor() {
		return valor;
	}

	public void setValor(final int valor) {
		this.valor = valor;
	}

	public Instant getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(final Instant fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

}
