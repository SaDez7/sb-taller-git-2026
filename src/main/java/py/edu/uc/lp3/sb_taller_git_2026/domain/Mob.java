package py.edu.uc.lp3.sb_taller_git_2026.domain;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Mob extends Entidad {

	protected Mob() {
		super();
	}

	public Mob(String id, int saludMaxima) {
		super(id, saludMaxima);
	}
}
