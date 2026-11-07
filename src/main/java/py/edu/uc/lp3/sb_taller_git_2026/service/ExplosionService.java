package py.edu.uc.lp3.sb_taller_git_2026.service;

import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ExplosionRequest;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ExplosionResponse;

public interface ExplosionService {

	ExplosionResponse simular(ExplosionRequest request);
}
