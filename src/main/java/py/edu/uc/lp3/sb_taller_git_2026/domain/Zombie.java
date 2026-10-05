package py.edu.uc.lp3.sb_taller_git_2026.domain;

public class Zombie extends Hostil {
	public Zombie(String id, int saludMaxima) {
		super(id, saludMaxima);
		setAgresivo((atacante, objetivo) -> {
			System.out.println(atacante.getId() + " ataca cuerpo a cuerpo a " + objetivo.getId());
			objetivo.recibirDano(5);
		});
	}
}
