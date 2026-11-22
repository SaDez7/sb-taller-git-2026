package py.edu.uc.lp3.sb_taller_git_2026.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos necesarios para crear un bloque persistido.")
public record BloqueRequest(
		@Schema(description = "Tipo de bloque.", example = "tierra")
		String tipo,
		@Schema(description = "Coordenada x.", example = "2")
		Double x,
		@Schema(description = "Coordenada y.", example = "0")
		Double y,
		@Schema(description = "Coordenada z.", example = "0")
		Double z) {
}
