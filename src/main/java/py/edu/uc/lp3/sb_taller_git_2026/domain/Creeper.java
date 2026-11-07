package py.edu.uc.lp3.sb_taller_git_2026.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import py.edu.uc.lp3.sb_taller_git_2026.domain.exception.ReglaDeJuegoException;

@Entity
@DiscriminatorValue("creeper")
public class Creeper extends Hostil {

	private static final int DANIO_MAXIMO = 20;

	protected Creeper() {
		super();
		configurarAtaque();
	}

	public Creeper(String id, int saludMaxima) {
		super(id, saludMaxima);
		configurarAtaque();
	}

	private void configurarAtaque() {
		setAgresivo((atacante, objetivo) -> {
			System.out.println(atacante.getId() + " explota junto a " + objetivo.getId());
			objetivo.recibirDano(DANIO_MAXIMO);
		});
	}

	public int danioDeExplosion(double distancia, double radio) {
		if (radio <= 0) {
			throw new ReglaDeJuegoException("El radio de la explosion debe ser mayor a 0.");
		}
		if (distancia < 0) {
			throw new ReglaDeJuegoException("La distancia a la explosion no puede ser negativa.");
		}
		if (distancia > radio) {
			return 0;
		}
		double factor = 1.0 - (distancia / radio);
		return Math.max(1, (int) Math.round(DANIO_MAXIMO * factor));
	}
}
