package py.edu.uc.lp3.sb_taller_git_2026.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.sb_taller_git_2026.service.ExplosionService;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ExplosionRequest;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ExplosionResponse;

@RestController
@RequestMapping("/simulacion/explosiones")
public class ExplosionController {

	private final ExplosionService service;

	public ExplosionController(ExplosionService service) {
		this.service = service;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.OK)
	public ExplosionResponse simular(@RequestBody ExplosionRequest request) {
		return service.simular(request);
	}
}
