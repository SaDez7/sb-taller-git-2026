package py.edu.uc.lp3.sb_taller_git_2026.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Table(name = "entidades")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)
public abstract class Entidad {

	@Id
	@Column(name = "id", nullable = false, length = 100)
	private String id;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "x", column = @Column(name = "posicion_x", nullable = false)),
			@AttributeOverride(name = "y", column = @Column(name = "posicion_y", nullable = false)),
			@AttributeOverride(name = "z", column = @Column(name = "posicion_z", nullable = false))
	})
	private Vector posicion;

	@Column(name = "salud", nullable = false)
	private int salud;

	@Column(name = "salud_maxima", nullable = false)
	private int saludMaxima;

	protected Entidad() {
		this.posicion = new Vector();
	}

	public Entidad(String id, int saludMaxima) {
		this.id = id;
		this.saludMaxima = saludMaxima;
		this.salud = saludMaxima;
		this.posicion = new Vector();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Vector getPosicion() {
		return posicion;
	}

	public void setPosicion(Vector posicion) {
		this.posicion = posicion;
	}

	public int getSalud() {
		return salud;
	}

	public void setSalud(int salud) {
		this.salud = salud;
	}

	public int getSaludMaxima() {
		return saludMaxima;
	}

	public void setSaludMaxima(int saludMaxima) {
		this.saludMaxima = saludMaxima;
	}
}
