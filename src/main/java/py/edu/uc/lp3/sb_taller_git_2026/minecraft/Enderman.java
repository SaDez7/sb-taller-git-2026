package py.edu.uc.lp3.sb_taller_git_2026.minecraft;

public class Enderman extends Hostil {
	public Enderman(String id, int saludMaxima) {
		super(id, saludMaxima);
		setAgresivo((atacante, objetivo) -> {
			System.out.println(atacante.getId() + " se teletransporta y ataca a " + objetivo.getId());
			objetivo.recibirDano(7);
		});
	}
}
