package py.edu.uc.lp3.sb_taller_git_2026.rest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.sb_taller_git_2026.service.BloqueService;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.BloqueResponse;

@RestController
@RequestMapping("/bloques")
public class BloqueController {

	private final BloqueService service;

	public BloqueController(BloqueService service) {
		this.service = service;
	}

	@GetMapping
	public List<BloqueResponse> listar() {
		return service.listar();
	}
}
