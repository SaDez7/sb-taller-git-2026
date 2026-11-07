package py.edu.uc.lp3.sb_taller_git_2026.service.dto;

public record EntidadRequest(
		String tipo,
		String id,
		Integer salud,
		Integer saludMaxima,
		Integer experiencia,
		Double x,
		Double y,
		Double z) {
}
