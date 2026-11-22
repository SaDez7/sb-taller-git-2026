package py.edu.uc.lp3.sb_taller_git_2026.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.sb_taller_git_2026.domain.Bloque;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Vector;
import py.edu.uc.lp3.sb_taller_git_2026.domain.exception.ReglaDeJuegoException;
import py.edu.uc.lp3.sb_taller_git_2026.repository.BloqueRepository;
import py.edu.uc.lp3.sb_taller_git_2026.service.BloqueService;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.BloqueResponse;

@Service
public class BloqueServiceImpl implements BloqueService {

	private final BloqueRepository repository;

	public BloqueServiceImpl(BloqueRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<BloqueResponse> listar() {
		return repository.findAll().stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional
	public BloqueResponse crear(String tipo, Vector posicion) {
		return toResponse(repository.save(new Bloque(validarTipo(tipo), validarPosicion(posicion))));
	}

	@Override
	public void actualizar(Bloque bloque, String tipo, Vector posicion) {
		if (bloque == null) {
			throw new IllegalArgumentException("El bloque no puede ser null.");
		}
		bloque.setTipo(validarTipo(tipo));
		bloque.setPosicion(validarPosicion(posicion));
	}

	@Override
	public Bloque aplicarExplosion(Bloque bloque, double distancia, double radio) {
		if (bloque == null) {
			throw new IllegalArgumentException("El bloque no puede ser null.");
		}
		validarRadio(radio);
		validarDistancia(distancia);
		boolean alcanzado = distancia <= radio;
		return new Bloque(bloque.getId(), bloque.getTipo(), bloque.getPosicion(), alcanzado);
	}

	private BloqueResponse toResponse(Bloque bloque) {
		return new BloqueResponse(
				bloque.getId(),
				bloque.getTipo(),
				bloque.isDestruido(),
				bloque.getPosicion().getX(),
				bloque.getPosicion().getY(),
				bloque.getPosicion().getZ());
	}

	private static String validarTipo(String tipo) {
		if (tipo == null || tipo.isBlank()) {
			throw new ReglaDeJuegoException("El tipo de bloque no puede estar vacío.");
		}
		return tipo.trim();
	}

	private static Vector validarPosicion(Vector posicion) {
		if (posicion == null) {
			throw new ReglaDeJuegoException("La posición del bloque no puede ser null.");
		}
		return posicion;
	}

	private static void validarRadio(double radio) {
		if (radio <= 0) {
			throw new ReglaDeJuegoException("El radio de la explosión debe ser mayor a 0.");
		}
	}

	private static void validarDistancia(double distancia) {
		if (distancia < 0) {
			throw new ReglaDeJuegoException("La distancia a la explosión no puede ser negativa.");
		}
	}
}
