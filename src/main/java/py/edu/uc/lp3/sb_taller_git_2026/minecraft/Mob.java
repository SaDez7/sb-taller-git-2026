package py.edu.uc.lp3.sb_taller_git_2026.minecraft;

import java.util.ArrayList;
import java.util.List;

public abstract class Mob extends Entidad {
	private ComportamientoIA ia;

	public Mob(String id, int saludMaxima) {
		super(id, saludMaxima);
	}

	public List<Object> generarBotin() {
		return new ArrayList<>();
	}

	public void deambular() {
		System.out.println(getId() + " deambula sin rumbo.");
	}

	public ComportamientoIA getIa() {
		return ia;
	}

	public void setIa(ComportamientoIA ia) {
		this.ia = ia;
	}
}
