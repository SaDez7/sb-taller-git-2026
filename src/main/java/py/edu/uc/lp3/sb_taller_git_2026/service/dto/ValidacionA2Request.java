package py.edu.uc.lp3.sb_taller_git_2026.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cuerpo que esta API envía a POST /api/explosiones de A2.")
public record ValidacionA2Request(
		@Schema(description = "Id de la TNT local de A2.", example = "t2")
		String tntId,
		@Schema(description = "Id del objetivo local de A2.", example = "CerdoPrueba")
		String objetivoId) {
}
