package py.edu.uc.lp3.sb_taller_git_2026.rest.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.sb_taller_git_2026.domain.Vector;
import py.edu.uc.lp3.sb_taller_git_2026.service.BloqueService;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.BloqueRequest;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.BloqueResponse;

@Tag(name = "Bloques", description = "Catálogo de bloques del dominio.")
@RestController
@RequestMapping("/bloques")
public class BloqueController {

	private final BloqueService service;

	public BloqueController(BloqueService service) {
		this.service = service;
	}

	@Operation(summary = "Listar bloques", description = "Devuelve todos los bloques disponibles.")
	@GetMapping
	public List<BloqueResponse> listar() {
		return service.listar();
	}

	@Operation(summary = "Crear bloque", description = "Persiste un bloque para poder referenciarlo desde la simulación.")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BloqueResponse crear(@RequestBody BloqueRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Los datos del bloque no pueden ser null.");
		}
		if (request.x() == null || request.y() == null || request.z() == null) {
			throw new IllegalArgumentException("La posición del bloque debe tener las coordenadas x, y y z.");
		}
		return service.crear(request.tipo(), new Vector(request.x(), request.y(), request.z()));
	}
}
