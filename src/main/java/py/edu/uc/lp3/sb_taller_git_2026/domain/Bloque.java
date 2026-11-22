package py.edu.uc.lp3.sb_taller_git_2026.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
		this(null, tipo, new Vector(), false);
	}

	public Bloque(String tipo, Vector posicion) {
		this(null, tipo, posicion, false);
	}

	public Bloque(Long id, String tipo, Vector posicion, boolean destruido) {
		this.id = id;
		this.tipo = tipo;
		this.posicion = posicion;
		this.destruido = destruido;
	}

	public Long getId() {
		return id;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Vector getPosicion() {
		return posicion;
	}

	public void setPosicion(Vector posicion) {
		this.posicion = posicion;
	}

	public boolean isDestruido() {
		return destruido;
	}
}
