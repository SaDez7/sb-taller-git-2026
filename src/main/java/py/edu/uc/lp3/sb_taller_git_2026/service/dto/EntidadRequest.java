package py.edu.uc.lp3.sb_taller_git_2026.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos necesarios para crear o actualizar una entidad.")
public record EntidadRequest(
		String tipo,
		String id,
		Integer salud,
		Integer saludMaxima,
		Integer experiencia,
		Double x,
		Double y,
		Double z) {
}
