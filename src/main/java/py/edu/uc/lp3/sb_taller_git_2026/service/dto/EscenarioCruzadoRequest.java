package py.edu.uc.lp3.sb_taller_git_2026.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Pedido para el escenario que combina las reglas de A2 y A3.")
public record EscenarioCruzadoRequest(
		@Schema(description = "Id del jugador vivo persistido en esta API.", example = "steve")
		String jugadorId,
		@Schema(description = "Id de la TNT creada en A2.", example = "t2")
		String tntId,
		@Schema(description = "Id de la entidad objetivo de A2.", example = "CerdoPrueba")
		String objetivoA2Id,
		@Schema(description = "Id del creeper de A3.", example = "4")
		Long creeperA3Id,
		@Schema(description = "Id del objetivo de A3.", example = "2")
		Long objetivoA3Id,
		@Schema(description = "Distancia del objetivo al creeper de A3, en bloques.", example = "2")
		int distanciaA3) {
}
