package py.edu.uc.lp3.sb_taller_git_2026.rest.controller;

import py.edu.uc.lp3.sb_taller_git_2026.domain.Jugador;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Vector;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class JugadorController {

	/**
	 * Construye un jugador con los parametros de la URL.
	 *
	 * <p>El controller no corrige ni completa nada: arma el constructor
	 * completo de {@link Jugador} y deja que la clase decida si los
	 * valores son validos.</p>
	 */
	@GetMapping("/jugador")
	public Jugador obtenerJugador(
			@RequestParam(defaultValue = "Steve") String id,
			@RequestParam(defaultValue = "20") int saludMaxima,
			@RequestParam(defaultValue = "0") int experiencia,
			@RequestParam(defaultValue = "0") double x,
			@RequestParam(defaultValue = "0") double y,
			@RequestParam(defaultValue = "0") double z) {

		return new Jugador(id, saludMaxima, experiencia, new Vector(x, y, z));
	}

	/**
	 * El dominio ya rechazo el dato; aca solo se traduce ese rechazo a
	 * un 400. No se repara ni se reintenta la construccion.
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> valorInvalido(IllegalArgumentException error) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getMessage());
	}
}
