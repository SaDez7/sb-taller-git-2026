package py.edu.uc.lp3.sb_taller_git_2026.service.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de A3 al explotar un creeper.")
public record ValidacionA3Response(
		Long creeperId,
		int vidaCreeper,
		List<Afectado> afectados) {

	@Schema(description = "Entidad afectada por la explosión en A3.")
	public record Afectado(
			Long id,
			String tipo,
			int danoRecibido,
			int vidaFinal) {
	}
}
