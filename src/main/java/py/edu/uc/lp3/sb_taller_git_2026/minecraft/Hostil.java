package py.edu.uc.lp3.sb_taller_git_2026.minecraft;

/**
 * Mob agresivo: ataca al jugador.
 *
 * <p>En el diagrama hereda de {@link Mob} y agrupa a {@code zombie},
 * {@code creeper}, {@code Esqueleto} y {@code Enderman}. El atributo
 * {@code agresivo} es del tipo {@link AtacaJugador}.</p>
 */
public abstract class Hostil extends Mob {
	private AtacaJugador agresivo;

	public Hostil(String id, int saludMaxima) {
		super(id, saludMaxima);
	}

	public void atacar(Entidad objetivo) {
		if (agresivo == null) {
			throw new IllegalStateException(getId() + " no tiene un ataque configurado.");
		}
		agresivo.atacar(this, objetivo);
	}

	public AtacaJugador getAgresivo() {
		return agresivo;
	}

	public void setAgresivo(AtacaJugador agresivo) {
		this.agresivo = agresivo;
	}
}
