package py.edu.uc.lp3.sb_taller_git_2026.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

@Entity
@DiscriminatorValue("jugador")
public class Jugador extends Entidad {
	private static final int SALUD_POR_DEFECTO = 20;

	@Transient
	private Inventario inventario;

	@Column(name = "experiencia")
	private int experiencia;

	protected Jugador() {
		super();
		this.inventario = new Inventario();
	}

	public Jugador(String id) {
		this(id, SALUD_POR_DEFECTO, 0, new Vector());
	}

	public Jugador(String id, int saludMaxima) {
		this(id, saludMaxima, 0, new Vector());
	}

	public Jugador(String id, int saludMaxima, int experiencia) {
		this(id, saludMaxima, experiencia, new Vector());
	}

	public Jugador(String id, int saludMaxima, int experiencia, Vector posicion) {
		super(id, saludMaxima);
		this.experiencia = experiencia;
		setPosicion(posicion);
		this.inventario = new Inventario();
	}

	public Inventario getInventario() {
		return inventario;
	}

	public void setInventario(Inventario inventario) {
		this.inventario = inventario;
	}

	public int getExperiencia() {
		return experiencia;
	}

	public void setExperiencia(int experiencia) {
		this.experiencia = experiencia;
	}
}
