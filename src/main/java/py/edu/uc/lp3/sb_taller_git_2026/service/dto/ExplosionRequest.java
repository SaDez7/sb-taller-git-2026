package py.edu.uc.lp3.sb_taller_git_2026.service.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Referencias a entidades persistidas para una simulación de explosión.")
public record ExplosionRequest(
		@Schema(description = "Id del creeper guardado por el CRUD.", example = "creeper-poo08")
		String creeperId,
		@Schema(description = "Radio de la explosión.", example = "5")
		Double radio,
		@Schema(description = "Jugadores guardados por el CRUD que reciben el daño.")
		List<JugadorReferencia> jugadores,
		@Schema(description = "Bloques guardados por el CRUD que pueden resultar destruidos.")
		List<BloqueReferencia> bloques) {

	@Schema(description = "Referencia a un jugador persistido.")
	public record JugadorReferencia(
			@Schema(description = "Id del jugador.", example = "steve")
			String id) {
	}

	@Schema(description = "Referencia a un bloque persistido.")
	public record BloqueReferencia(
			@Schema(description = "Id del bloque.", example = "1")
			Long id) {
	}
}
