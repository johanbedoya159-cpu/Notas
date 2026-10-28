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
@Table(name = "nota")
public class NotaEntidadJPA {

	@Id
	@Column(name = "id")
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "id_grupo")
	private GrupoEntidadJPA grupo;

	@Column(name = "nombre")
	private String nombre;

	@Column(name = "fecha_inicio_registro")
	private Instant fechaInicioRegistro;

	@Column(name = "fecha_fin_registro")
	private Instant fechaFinRegistro;

	public UUID getId() {
		return id;
	}

	public void setId(final UUID id) {
		this.id = id;
	}

	public GrupoEntidadJPA getGrupo() {
		return grupo;
	}

	public void setGrupo(final GrupoEntidadJPA grupo) {
		this.grupo = grupo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(final String nombre) {
		this.nombre = nombre;
	}

	public Instant getFechaInicioRegistro() {
		return fechaInicioRegistro;
	}

	public void setFechaInicioRegistro(final Instant fechaInicioRegistro) {
		this.fechaInicioRegistro = fechaInicioRegistro;
	}

	public Instant getFechaFinRegistro() {
		return fechaFinRegistro;
	}

	public void setFechaFinRegistro(final Instant fechaFinRegistro) {
		this.fechaFinRegistro = fechaFinRegistro;
	}

}
