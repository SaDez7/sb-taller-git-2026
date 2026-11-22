package py.edu.uc.lp3.sb_taller_git_2026.domain;

public class Aldeano extends Pacifico {
	private Profesion profesion;

	public Aldeano(String id, int saludMaxima, Profesion profesion) {
		super(id, saludMaxima);
		this.profesion = profesion;
	}

	public Profesion getProfesion() {
		return profesion;
	}

	public void setProfesion(Profesion profesion) {
		this.profesion = profesion;
	}
}
