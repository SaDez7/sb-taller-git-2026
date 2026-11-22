package py.edu.uc.lp3.sb_taller_git_2026.service;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Comportamiento de una entidad persistida.")
public interface ComportamientoIA {

	@Schema(description = "Tipo concreto de la entidad.", example = "Creeper")
	String tipo();

	@Schema(description = "Descripción del comportamiento calculada por el service.")
	String descripcion();
}
