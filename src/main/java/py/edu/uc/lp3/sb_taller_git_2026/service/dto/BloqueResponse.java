package py.edu.uc.lp3.sb_taller_git_2026.service.dto;

public record BloqueResponse(
		Long id,
		String tipo,
		boolean destruido,
		double x,
		double y,
		double z) {
}
