package py.edu.uc.lp3.sb_taller_git_2026.service;

import java.util.List;

import py.edu.uc.lp3.sb_taller_git_2026.domain.Jugador;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.EntidadRequest;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.EntidadResponse;

public interface EntidadService {

	List<EntidadResponse> listar();

	List<EntidadResponse> listarVivos();

	EntidadResponse obtener(String id);

	EntidadResponse crear(EntidadRequest request);

	EntidadResponse actualizar(String id, EntidadRequest request);

	void eliminar(String id);

	Jugador construirJugador(String id, int saludMaxima, int experiencia, double x, double y, double z);
}
