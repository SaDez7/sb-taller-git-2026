package py.edu.uc.lp3.sb_taller_git_2026.rest.controller;

import py.edu.uc.lp3.sb_taller_git_2026.domain.Jugador;
import py.edu.uc.lp3.sb_taller_git_2026.service.EntidadService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Jugador", description = "Construcción de jugadores a partir de parámetros de consulta.")
@RestController
public class JugadorController {

	private final EntidadService service;

	public JugadorController(EntidadService service) {
		this.service = service;
	}

	@Operation(summary = "Obtener jugador", description = "El service valida los parámetros de la URL y construye el jugador.")
	@GetMapping("/jugador")
	public Jugador obtenerJugador(
			@Parameter(description = "Identificador del jugador.") @RequestParam(defaultValue = "Steve") String id,
			@Parameter(description = "Salud máxima del jugador.") @RequestParam(defaultValue = "20") int saludMaxima,
			@Parameter(description = "Experiencia inicial del jugador.") @RequestParam(defaultValue = "0") int experiencia,
			@Parameter(description = "Coordenada X de la posición inicial.") @RequestParam(defaultValue = "0") double x,
			@Parameter(description = "Coordenada Y de la posición inicial.") @RequestParam(defaultValue = "0") double y,
			@Parameter(description = "Coordenada Z de la posición inicial.") @RequestParam(defaultValue = "0") double z) {

		return service.construirJugador(id, saludMaxima, experiencia, x, y, z);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> valorInvalido(IllegalArgumentException error) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getMessage());
	}
}
