package py.edu.uc.lp3.sb_taller_git_2026.rest.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.sb_taller_git_2026.service.ComportamientoIA;
import py.edu.uc.lp3.sb_taller_git_2026.service.MobService;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.AtaqueResponse;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ComportamientoResponse;

@Tag(name = "Mobs", description = "Demostración de comportamientos de entidades persistidas desde la capa service.")
@RestController
public class MobController {

	private final MobService service;

	public MobController(MobService service) {
		this.service = service;
	}

	@Operation(summary = "Simular ataques", description = "El service calcula el daño de cada mob y devuelve el efecto sobre el objetivo.")
	@GetMapping("/mobs/ataque")
	public List<AtaqueResponse> ataques() {
		return service.simularAtaques();
	}

	@Operation(summary = "Listar comportamientos", description = "Lee las entidades persistidas de H2 y devuelve su comportamiento a través de ComportamientoIA.")
	@GetMapping("/mobs/comportamiento")
	public List<ComportamientoResponse> comportamientos() {
		return service.listarComportamientos().stream()
				.map(comportamiento -> new ComportamientoResponse(
						comportamiento.tipo(),
						comportamiento.descripcion()))
				.toList();
	}
}
