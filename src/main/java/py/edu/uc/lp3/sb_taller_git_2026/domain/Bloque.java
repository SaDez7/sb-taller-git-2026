package py.edu.uc.lp3.sb_taller_git_2026.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import py.edu.uc.lp3.sb_taller_git_2026.domain.exception.ReglaDeJuegoException;

@Entity
@Table(name = "bloques")
public class Bloque {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "tipo", nullable = false, length = 100)
	private String tipo;

	@Embedded
	private Vector posicion;

	@Column(name = "destruido", nullable = false)
	private boolean destruido;

	protected Bloque() {
		this.tipo = "desconocido";
		this.posicion = new Vector();
	}

	public Bloque(String tipo) {
		this(tipo, new Vector());
	}

	public Bloque(String tipo, Vector posicion) {
		this.tipo = tipo;
		this.posicion = posicion == null ? new Vector() : posicion;
		this.destruido = false;
	}

	public boolean destruidoPorExplosion(double distancia, double radio) {
		if (radio <= 0) {
			throw new ReglaDeJuegoException("El radio de la explosion debe ser mayor a 0.");
		}
		if (distancia < 0) {
			throw new ReglaDeJuegoException("La distancia a la explosion no puede ser negativa.");
		}
		boolean alcanzado = distancia <= radio;
		if (alcanzado) {
			this.destruido = true;
		}
		return alcanzado;
	}

	public Long getId() {
		return id;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		if (tipo == null || tipo.isBlank()) {
			throw new ReglaDeJuegoException("El tipo de bloque no puede estar vacio.");
		}
		this.tipo = tipo;
	}

	public Vector getPosicion() {
		return posicion;
	}

	public void setPosicion(Vector posicion) {
		if (posicion == null) {
			throw new ReglaDeJuegoException("La posicion del bloque no puede ser null.");
		}
		this.posicion = posicion;
	}

	public boolean isDestruido() {
		return destruido;
	}

	public void setDestruido(boolean destruido) {
		this.destruido = destruido;
	}
}
