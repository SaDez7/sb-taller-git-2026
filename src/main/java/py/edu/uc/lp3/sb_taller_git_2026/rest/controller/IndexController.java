package py.edu.uc.lp3.sb_taller_git_2026.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Salud", description = "Estado básico del servicio.")
@RestController
public class IndexController {

	@Operation(summary = "Verificar estado", description = "Devuelve un mensaje que confirma que la aplicación está viva.")
	@GetMapping("/")
	public String index() {
		return "Servicio Spring Boot vivo. Dominio: Minecraft.";
	}
}
