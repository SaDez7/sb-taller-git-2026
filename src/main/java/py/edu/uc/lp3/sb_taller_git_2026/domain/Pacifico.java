package py.edu.uc.lp3.sb_taller_git_2026.domain;

public abstract class Pacifico extends Mob {
	private boolean pacifico = true;

	public Pacifico(String id, int saludMaxima) {
		super(id, saludMaxima);
	}

	public boolean isPacifico() {
		return pacifico;
	}

	public void setPacifico(boolean pacifico) {
		this.pacifico = pacifico;
	}
}
