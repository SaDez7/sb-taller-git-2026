package py.edu.uc.lp3.sb_taller_git_2026.domain;

public class Zombie extends Hostil implements ComportamientoIA {

	public Zombie(String id, int saludMaxima) {
		super(id, saludMaxima);
		setAgresivo((atacante, objetivo) -> {
			System.out.println(atacante.getId() + " ataca cuerpo a cuerpo a " + objetivo.getId());
			objetivo.recibirDano(5);
		});
	}

	@Override
	public void actuar(Mob mob) {
		mob.deambular();
	}

	@Override
	public String describir() {
		return "Zombie persigue y ataca cuerpo a cuerpo.";
	}
}
