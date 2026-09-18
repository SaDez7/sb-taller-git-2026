package py.edu.uc.lp3.sb_taller_git_2026.minecraft;

/**
 * Mob pacífico: no ataca al jugador.
 *
 * <p>En el diagrama figura como {@code Classname}; se renombra a
 * {@code Pacifico}. Hereda de {@link Mob} y agrupa a {@link Aldeano} y
 * {@link Cerdo}.</p>
 */
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
