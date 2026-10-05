package py.edu.uc.lp3.sb_taller_git_2026.domain;

/**
 * Mob agresivo: ataca al jugador.
 *
 * <p>En el diagrama hereda de {@link Mob} y agrupa a {@code Zombie},
 * {@code Creeper}, {@code Esqueleto} y {@code Enderman}. El atributo
 * {@code agresivo} es del tipo {@link AtacaJugador}.</p>
 */
public abstract class Hostil extends Mob {
	private static final int ALCANCE_MAXIMO = 3;

	private AtacaJugador agresivo;

	public Hostil(String id, int saludMaxima) {
		super(id, saludMaxima);
	}

	/**
	 * Ataque sin datos de alcance: el hostil dispara a ciegas.
	 */
	public void atacar() {
		atacar(ALCANCE_MAXIMO);
	}

	/**
	 * Ataque a cierta distancia: solo impacta si el objetivo esta al
	 * alcance. Sobrecarga de {@link #atacar(Entidad)}, mismo nombre y
	 * otra lista de argumentos.
	 *
	 * @param distancia bloques entre este hostil y el objetivo
	 * @throws IllegalArgumentException si la distancia es negativa
	 */
	public void atacar(int distancia) {
		if (distancia < 0) {
			throw new IllegalArgumentException(
					"La distancia no puede ser negativa, se recibio: " + distancia);
		}
		if (distancia <= ALCANCE_MAXIMO) {
			System.out.println(getId() + " alcanza a atacar a " + distancia + " bloque(s).");
		} else {
			System.out.println(getId() + " no alcanza: el objetivo esta a " + distancia + " bloque(s).");
		}
	}

	@Override
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
