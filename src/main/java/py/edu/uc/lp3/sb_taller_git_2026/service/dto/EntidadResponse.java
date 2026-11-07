package py.edu.uc.lp3.sb_taller_git_2026.service.dto;

public record EntidadResponse(
		String tipo,
		String id,
		int salud,
		int saludMaxima,
		int experiencia,
		double x,
		double y,
		double z) {
}
