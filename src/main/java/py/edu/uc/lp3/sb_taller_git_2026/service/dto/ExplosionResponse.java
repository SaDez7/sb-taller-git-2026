package py.edu.uc.lp3.sb_taller_git_2026.service.dto;

import java.util.List;

public record ExplosionResponse(
		String creeperId,
		double radio,
		List<JugadorAfectado> jugadores,
		List<BloqueAfectado> bloques) {

	public record JugadorAfectado(
			String id,
			double distancia,
			boolean alcanzado,
			int danio,
			int saludAntes,
			int saludDespues) {
	}

	public record BloqueAfectado(Long id, String tipo, double distancia, boolean destruido) {
	}
}
