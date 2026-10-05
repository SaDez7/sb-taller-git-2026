package py.edu.uc.lp3.sb_taller_git_2026.rest.controller;

import java.util.ArrayList;
import java.util.List;

import py.edu.uc.lp3.sb_taller_git_2026.domain.Aldeano;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Entidad;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Jugador;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Profesion;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Zombie;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MobController {

	/**
	 * Le pide el metodo abstracto a cada mob y responde JSON.
	 *
	 * <p>Los mobs se guardan como {@link Entidad}, el tipo donde vive
	 * el metodo abstracto, y se invoca {@code atacar(Entidad)} sobre esa
	 * referencia: cada objeto ejecuta su propia implementacion. No hay
	 * ni un if ni un switch por tipo, el controller no sabe que clase
	 * concreta esta atacando.</p>
	 */
	@GetMapping("/mobs/ataque")
	public List<Ataque> ataques() {
		Entidad objetivo = new Jugador("Steve", 20);
		List<Entidad> mobs = List.of(
				new Zombie("zombie-lab", 20),
				new Aldeano("aldeano-lab", 20, Profesion.GRANJERO));

		List<Ataque> resultado = new ArrayList<>();
		for (Entidad mob : mobs) {
			int saludAntes = objetivo.getSalud();
			mob.atacar(objetivo);
			resultado.add(new Ataque(
					mob.getId(),
					mob.getClass().getSimpleName(),
					saludAntes,
					objetivo.getSalud()));
		}
		return resultado;
	}

	/**
	 * Como respondio cada mob al objetivo.
	 */
	public record Ataque(String mob, String tipo, int saludObjetivoAntes, int saludObjetivoDespues) {
	}
}
