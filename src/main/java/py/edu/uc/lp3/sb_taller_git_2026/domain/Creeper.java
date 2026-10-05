package py.edu.uc.lp3.sb_taller_git_2026.domain;

public class Creeper extends Hostil {
	public Creeper(String id, int saludMaxima) {
		super(id, saludMaxima);
		setAgresivo((atacante, objetivo) -> {
			System.out.println(atacante.getId() + " explota junto a " + objetivo.getId());
			objetivo.recibirDano(20);
		});
	}
}
