package py.edu.uc.lp3.sb_taller_git_2026.minecraft;

/**
 * Comportamiento agresivo de un {@link Hostil} hacia el jugador.
 *
 * <p>En el diagrama de clases figura como el tipo {@code atacaJugador},
 * atributo {@code agresivo} de {@link Hostil}.</p>
 */
@FunctionalInterface
public interface AtacaJugador {
	void atacar(Entidad atacante, Entidad objetivo);
}
