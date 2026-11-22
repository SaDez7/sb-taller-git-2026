package py.edu.uc.lp3.sb_taller_git_2026.rest.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.sb_taller_git_2026.service.ObjetoExternoService;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ObjetoExternoResponse;
import reactor.core.publisher.Mono;

@Tag(name = "API externa", description = "Consulta a la API REST pública mediante WebClient.")
@RestController
@RequestMapping("/objetos-externos")
public class ObjetoExternoController {

	private final ObjetoExternoService service;

	public ObjetoExternoController(ObjetoExternoService service) {
		this.service = service;
	}

	@Operation(summary = "Listar objetos externos", description = "Obtiene la lista de objetos de https://api.restful-api.dev/objects mediante WebClient.")
	@GetMapping
	public Mono<List<ObjetoExternoResponse>> listar() {
		return service.listar();
	}
}
