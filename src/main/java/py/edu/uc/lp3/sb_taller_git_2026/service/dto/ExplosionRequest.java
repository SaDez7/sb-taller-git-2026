package py.edu.uc.lp3.sb_taller_git_2026.service.dto;

import java.util.List;

public record ExplosionRequest(
		String creeperId,
		Integer saludMaximaCreeper,
		Double radio,
		Posicion origen,
		List<JugadorRequest> jugadores,
		List<BloqueRequest> bloques) {

	public record Posicion(Double x, Double y, Double z) {
	}

	public record JugadorRequest(String id, Integer salud, Integer saludMaxima, Posicion posicion) {
	}

	public record BloqueRequest(Long id, String tipo, Posicion posicion) {
	}
}
