package py.edu.uc.lp3.sb_taller_git_2026.service.dto;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Objeto devuelto por la API externa REST.")
public record ObjetoExternoResponse(
		String id,
		String name,
		Map<String, Object> data) {
}
