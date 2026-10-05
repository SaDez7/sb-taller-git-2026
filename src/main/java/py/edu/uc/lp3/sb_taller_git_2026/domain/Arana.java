package py.edu.uc.lp3.sb_taller_git_2026.domain;

public class Arana extends Hostil {
	public Arana(String id, int saludMaxima) {
		super(id, saludMaxima);
		setAgresivo((atacante, objetivo) -> {
			System.out.println(atacante.getId() + " muerde a " + objetivo.getId());
			objetivo.recibirDano(3);
		});
	}

	@Override
	public void moverse() {
		System.out.println(getId() + " trepa por una superficie.");
	}
}
