package py.edu.uc.lp3.sb_taller_git_2026.domain;

import java.util.ArrayList;
import java.util.List;

public class Aldeano extends Pacifico implements ComportamientoIA {
	private Profesion profesion;

	public Aldeano(String id, int saludMaxima, Profesion profesion) {
		super(id, saludMaxima);
		this.profesion = profesion;
	}

	public List<Object> ofrecerTrueques() {
		return new ArrayList<>();
	}

	@Override
	public void actuar(Mob mob) {
		mob.deambular();
	}

	@Override
	public String describir() {
		return "Aldeano deambula por el poblado y ofrece trueques.";
	}

	public Profesion getProfesion() {
		return profesion;
	}

	public void setProfesion(Profesion profesion) {
		this.profesion = profesion;
	}
}
