package py.edu.uc.lp3.sb_taller_git_2026.minecraft;

public class zombie extends Hostil {
	public zombie(String id, int saludMaxima) {
		super(id, saludMaxima);
		setAgresivo((atacante, objetivo) -> {
			System.out.println(atacante.getId() + " ataca cuerpo a cuerpo a " + objetivo.getId());
			objetivo.recibirDano(5);
		});
	}
}
