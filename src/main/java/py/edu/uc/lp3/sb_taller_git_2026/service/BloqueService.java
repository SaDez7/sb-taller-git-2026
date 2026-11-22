package py.edu.uc.lp3.sb_taller_git_2026.service;

import java.util.List;

import py.edu.uc.lp3.sb_taller_git_2026.domain.Bloque;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Vector;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.BloqueResponse;

public interface BloqueService {

	List<BloqueResponse> listar();

	BloqueResponse crear(String tipo, Vector posicion);

	void actualizar(Bloque bloque, String tipo, Vector posicion);

	Bloque aplicarExplosion(Bloque bloque, double distancia, double radio);
}
