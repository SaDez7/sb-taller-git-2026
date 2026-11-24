package py.edu.uc.lp3.sb_taller_git_2026.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de A2 al registrar una explosión de TNT.")
public record ValidacionA2Response(
		Long id,
		String tntId,
		String objetivoId,
		int saludAnterior,
		int saludActual,
		int danoAplicado) {
}
