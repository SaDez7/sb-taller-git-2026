package py.edu.uc.lp3.sb_taller_git_2026.rest.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.sb_taller_git_2026.service.EscenarioCruzadoService;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.EscenarioCruzadoRequest;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.EscenarioCruzadoResponse;

@Tag(name = "Escenario cruzado", description = "Escenario de POO-09 que combina las reglas de A2 y A3.")
@RestController
@RequestMapping("/escenarios/explosion-cruzada")
public class EscenarioCruzadoController {

	private final EscenarioCruzadoService service;

	public EscenarioCruzadoController(EscenarioCruzadoService service) {
		this.service = service;
	}

	@Operation(summary = "Ejecutar escenario cruzado",
			description = "Valida la TNT en A2 y el creeper en A3; guarda la jugada sólo si ambos aceptan.")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EscenarioCruzadoResponse ejecutar(@RequestBody EscenarioCruzadoRequest request) {
		return service.ejecutar(request);
	}

	@Operation(summary = "Listar escenarios guardados", description = "Devuelve las jugadas aceptadas por A2 y A3.")
	@GetMapping
	public List<EscenarioCruzadoResponse> listar() {
		return service.listar();
	}

	@Operation(summary = "Obtener escenario por id", description = "Lee una jugada cruzada persistida en esta API.")
	@GetMapping("/{id}")
	public EscenarioCruzadoResponse obtener(@PathVariable Long id) {
		return service.obtener(id);
	}
}
