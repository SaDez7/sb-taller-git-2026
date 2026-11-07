package py.edu.uc.lp3.sb_taller_git_2026.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

@MappedSuperclass
public abstract class Hostil extends Mob {
	private static final int ALCANCE_MAXIMO = 3;

	@Transient
	@JsonIgnore
	private AtacaJugador agresivo;

	protected Hostil() {
		super();
	}

	public Hostil(String id, int saludMaxima) {
		super(id, saludMaxima);
	}

	public void atacar() {
		atacar(ALCANCE_MAXIMO);
	}

	public void atacar(int distancia) {
		if (distancia < 0) {
			throw new IllegalArgumentException(
					"La distancia no puede ser negativa, se recibio: " + distancia);
		}
		if (distancia <= ALCANCE_MAXIMO) {
			System.out.println(getId() + " alcanza a atacar a " + distancia + " bloque(s).");
		} else {
			System.out.println(getId() + " no alcanza: el objetivo esta a " + distancia + " bloque(s).");
		}
	}

	@Override
	public void atacar(Entidad objetivo) {
		if (agresivo == null) {
			throw new IllegalStateException(getId() + " no tiene un ataque configurado.");
		}
		agresivo.atacar(this, objetivo);
	}

	public AtacaJugador getAgresivo() {
		return agresivo;
	}

	public void setAgresivo(AtacaJugador agresivo) {
		this.agresivo = agresivo;
	}
}
