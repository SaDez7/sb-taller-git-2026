package py.edu.uc.lp3.sb_taller_git_2026.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("creeper")
public class Creeper extends Hostil {

	protected Creeper() {
		super();
	}

	public Creeper(String id, int saludMaxima) {
		super(id, saludMaxima);
	}
}
