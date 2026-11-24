package py.edu.uc.lp3.sb_taller_git_2026.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Jugada cruzada guardada después de que A2 y A3 aceptaron.")
public record EscenarioCruzadoResponse(
		@Schema(description = "Id de la jugada guardada en esta API.", example = "1")
		Long id,
		String jugadorId,
		String tntId,
		String objetivoA2Id,
		Long explosionA2Id,
		int danoA2,
		int saludA2Despues,
		Long creeperA3Id,
		Long objetivoA3Id,
		int distanciaA3,
		int danoA3,
		int vidaA3Despues) {
}
