package py.edu.uc.lp3.sb_taller_git_2026.minecraft;

import java.util.ArrayList;
import java.util.List;

public class Aldeano extends Pacifico {
	private Profesion profesion;

	public Aldeano(String id, int saludMaxima, Profesion profesion) {
		super(id, saludMaxima);
		this.profesion = profesion;
	}

	public List<Object> ofrecerTrueques() {
		return new ArrayList<>();
	}

	public Profesion getProfesion() {
		return profesion;
	}

	public void setProfesion(Profesion profesion) {
		this.profesion = profesion;
	}
}
