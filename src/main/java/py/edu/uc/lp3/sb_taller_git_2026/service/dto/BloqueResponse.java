package py.edu.uc.lp3.sb_taller_git_2026.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representación de un bloque persistido.")
public record BloqueResponse(
		Long id,
		String tipo,
		boolean destruido,
		double x,
		double y,
		double z) {
}
