package py.edu.uc.lp3.sb_taller_git_2026.rest.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

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

@Tag(name = "Entidades", description = "CRUD REST de entidades persistidas.")
@RestController
@RequestMapping("/entidades")
public class EntidadController {

	private final EntidadService service;

	public EntidadController(EntidadService service) {
		this.service = service;
	}

	@Operation(summary = "Listar entidades", description = "Devuelve todas las entidades persistidas.")
	@GetMapping
	public List<EntidadResponse> listar() {
		return service.listar();
	}

	@Operation(summary = "Listar entidades vivas", description = "Devuelve solo las entidades con salud mayor que cero.")
	@GetMapping("/vivos")
	public List<EntidadResponse> listarVivos() {
		return service.listarVivos();
	}

	@Operation(summary = "Obtener entidad", description = "Devuelve una entidad por su identificador.")
	@GetMapping("/{id}")
	public EntidadResponse obtener(@Parameter(description = "Identificador de la entidad.") @PathVariable String id) {
		return service.obtener(id);
	}

	@Operation(summary = "Crear entidad", description = "Crea una entidad nueva con el cuerpo recibido.")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EntidadResponse crear(@RequestBody EntidadRequest request) {
		return service.crear(request);
	}

	@Operation(summary = "Actualizar entidad", description = "Reemplaza los datos de una entidad existente.")
	@PutMapping("/{id}")
	public EntidadResponse actualizar(@Parameter(description = "Identificador de la entidad.") @PathVariable String id,
			@RequestBody EntidadRequest request) {
		return service.actualizar(id, request);
	}

	@Operation(summary = "Eliminar entidad", description = "Elimina una entidad por su identificador.")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminar(@Parameter(description = "Identificador de la entidad.") @PathVariable String id) {
		service.eliminar(id);
	}
}
