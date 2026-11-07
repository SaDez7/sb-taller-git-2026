package py.edu.uc.lp3.sb_taller_git_2026.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.sb_taller_git_2026.domain.Bloque;
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

	private BloqueResponse toResponse(Bloque bloque) {
		return new BloqueResponse(
				bloque.getId(),
				bloque.getTipo(),
				bloque.isDestruido(),
				bloque.getPosicion().getX(),
				bloque.getPosicion().getY(),
				bloque.getPosicion().getZ());
	}
}
