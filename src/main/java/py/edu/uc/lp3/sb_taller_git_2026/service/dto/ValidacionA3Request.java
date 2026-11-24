package py.edu.uc.lp3.sb_taller_git_2026.service.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cuerpo que esta API envía a POST /entidades/{id}/explotar de A3.")
public record ValidacionA3Request(
		@Schema(description = "Objetivos alcanzados por el creeper de A3.")
		List<Objetivo> objetivos) {

	@Schema(description = "Objetivo de la explosión en A3.")
	public record Objetivo(
			@Schema(description = "Id persistido en A3.", example = "2")
			Long id,
			@Schema(description = "Distancia al creeper, en bloques.", example = "2")
			int distancia) {
	}
}
