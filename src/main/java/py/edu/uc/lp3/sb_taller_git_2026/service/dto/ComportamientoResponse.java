package py.edu.uc.lp3.sb_taller_git_2026.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import py.edu.uc.lp3.sb_taller_git_2026.service.ComportamientoIA;

@Schema(description = "Descripción del comportamiento de una entidad persistida.")
public record ComportamientoResponse(
		String tipo,
		String descripcion) implements ComportamientoIA {
}
