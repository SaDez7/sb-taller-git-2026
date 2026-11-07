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

import py.edu.uc.lp3.sb_taller_git_2026.domain.exception.ReglaDeJuegoException;

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
		if (id == null || id.isBlank()) {
			throw new IllegalArgumentException("El id de la entidad no puede estar vacio.");
		}
		if (saludMaxima <= 0) {
			throw new ReglaDeJuegoException("La salud maxima debe ser mayor a 0.");
		}
		this.id = id;
		this.saludMaxima = saludMaxima;
		this.salud = saludMaxima;
		this.posicion = new Vector();
	}

	public abstract void atacar(Entidad objetivo);

	public void moverse() {
		System.out.println(getId() + " se mueve.");
	}

	public void recibirDano(int cantidad) {
		this.salud = Math.max(0, this.salud - cantidad);
	}

	public void curar(int cantidad) {
		this.salud = Math.min(getSaludMaxima(), this.salud + cantidad);
	}

	public boolean estaVivo() {
		return salud > 0;
	}

	public void actualizarEstado(int salud, int saludMaxima, Vector posicion) {
		if (saludMaxima <= 0) {
			throw new ReglaDeJuegoException("La salud maxima debe ser mayor a 0.");
		}
		if (salud < 0 || salud > saludMaxima) {
			throw new ReglaDeJuegoException("La salud debe estar entre 0 y la salud maxima.");
		}
		if (posicion == null) {
			throw new ReglaDeJuegoException("La posicion no puede ser null.");
		}
		this.saludMaxima = saludMaxima;
		this.salud = salud;
		this.posicion = posicion;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		if (id == null || id.isBlank()) {
			throw new ReglaDeJuegoException("El id de la entidad no puede estar vacio.");
		}
		this.id = id;
	}

	public Vector getPosicion() {
		return posicion;
	}

	public void setPosicion(Vector posicion) {
		if (posicion == null) {
			throw new ReglaDeJuegoException("La posicion no puede ser null.");
		}
		this.posicion = posicion;
	}

	public int getSalud() {
		return salud;
	}

	public void setSalud(int salud) {
		if (salud < 0 || salud > saludMaxima) {
			throw new ReglaDeJuegoException("La salud debe estar entre 0 y la salud maxima.");
		}
		this.salud = salud;
	}

	public int getSaludMaxima() {
		return saludMaxima;
	}

	public void setSaludMaxima(int saludMaxima) {
		if (saludMaxima <= 0) {
			throw new ReglaDeJuegoException("La salud maxima debe ser mayor a 0.");
		}
		if (salud > saludMaxima) {
			throw new ReglaDeJuegoException("La salud actual no puede superar la salud maxima.");
		}
		this.saludMaxima = saludMaxima;
	}
}
