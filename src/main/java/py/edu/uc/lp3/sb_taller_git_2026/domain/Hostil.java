package py.edu.uc.lp3.sb_taller_git_2026.domain;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Hostil extends Mob {

	protected Hostil() {
		super();
	}

	public Hostil(String id, int saludMaxima) {
		super(id, saludMaxima);
	}
}
