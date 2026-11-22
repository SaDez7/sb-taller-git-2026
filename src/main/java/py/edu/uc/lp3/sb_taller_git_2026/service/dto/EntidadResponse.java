package py.edu.uc.lp3.sb_taller_git_2026.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representación de una entidad persistida.")
public record EntidadResponse(
		String tipo,
		String id,
		int salud,
		int saludMaxima,
		int experiencia,
		double x,
		double y,
		double z) {
}
