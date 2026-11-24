package py.edu.uc.lp3.sb_taller_git_2026.service;

import java.util.List;

import py.edu.uc.lp3.sb_taller_git_2026.service.dto.EscenarioCruzadoRequest;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.EscenarioCruzadoResponse;

public interface EscenarioCruzadoService {

	EscenarioCruzadoResponse ejecutar(EscenarioCruzadoRequest request);

	List<EscenarioCruzadoResponse> listar();

	EscenarioCruzadoResponse obtener(Long id);
}
