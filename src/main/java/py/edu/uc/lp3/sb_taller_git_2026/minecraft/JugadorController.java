package py.edu.uc.lp3.sb_taller_git_2026.minecraft;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class JugadorController {

	@GetMapping("/jugador")
	public Jugador obtenerJugador(
			@RequestParam(defaultValue = "Steve") String id,
			@RequestParam(defaultValue = "20") int saludMaxima,
			@RequestParam(defaultValue = "0") int experiencia,
			@RequestParam(defaultValue = "0") double x,
			@RequestParam(defaultValue = "0") double y,
			@RequestParam(defaultValue = "0") double z) {

		Jugador jugador = new Jugador(id, saludMaxima);
		jugador.setExperiencia(experiencia);
		jugador.setPosicion(new Vector(x, y, z));

		return jugador;
	}
}
