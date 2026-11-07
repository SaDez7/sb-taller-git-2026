package py.edu.uc.lp3.sb_taller_git_2026.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.sb_taller_git_2026.service.EntidadService;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.EntidadRequest;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.EntidadResponse;

@RestController
@RequestMapping("/entidades")
public class EntidadController {

	private final EntidadService service;

	public EntidadController(EntidadService service) {
		this.service = service;
	}

	@GetMapping
	public List<EntidadResponse> listar() {
		return service.listar();
	}

	@GetMapping("/vivos")
	public List<EntidadResponse> listarVivos() {
		return service.listarVivos();
	}

	@GetMapping("/{id}")
	public EntidadResponse obtener(@PathVariable String id) {
		return service.obtener(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EntidadResponse crear(@RequestBody EntidadRequest request) {
		return service.crear(request);
	}

	@PutMapping("/{id}")
	public EntidadResponse actualizar(@PathVariable String id, @RequestBody EntidadRequest request) {
		return service.actualizar(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable String id) {
		service.eliminar(id);
	}
}
