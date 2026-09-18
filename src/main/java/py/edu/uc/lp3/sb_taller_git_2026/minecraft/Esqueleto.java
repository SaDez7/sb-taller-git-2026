package py.edu.uc.lp3.sb_taller_git_2026.minecraft;

public class Esqueleto extends Hostil {
	public Esqueleto(String id, int saludMaxima) {
		super(id, saludMaxima);
		setAgresivo((atacante, objetivo) -> {
			System.out.println(atacante.getId() + " dispara una flecha a " + objetivo.getId());
			objetivo.recibirDano(4);
		});
	}
}
