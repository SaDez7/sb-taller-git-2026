package py.edu.uc.lp3.sb_taller_git_2026.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import py.edu.uc.lp3.sb_taller_git_2026.domain.Aldeano;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Creeper;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Entidad;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Jugador;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Profesion;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Zombie;
import py.edu.uc.lp3.sb_taller_git_2026.repository.EntidadRepository;
import py.edu.uc.lp3.sb_taller_git_2026.service.ComportamientoIA;
import py.edu.uc.lp3.sb_taller_git_2026.service.MobService;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.AtaqueResponse;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ComportamientoResponse;

@Service
public class MobServiceImpl implements MobService {

	private static final int DANIO_ZOMBIE = 5;

	private final EntidadRepository entidadRepository;

	public MobServiceImpl(EntidadRepository entidadRepository) {
		this.entidadRepository = entidadRepository;
	}

	@Override
	public List<AtaqueResponse> simularAtaques() {
		Entidad objetivo = new Jugador("Steve", 20);
		List<Entidad> atacantes = List.of(
				new Zombie("zombie-lab", 20),
				new Aldeano("aldeano-lab", 20, Profesion.GRANJERO));

		List<AtaqueResponse> resultado = new ArrayList<>();
		for (Entidad atacante : atacantes) {
			int saludAntes = objetivo.getSalud();
			int danio = danioAtaque(atacante);
			objetivo.setSalud(Math.max(0, objetivo.getSalud() - danio));
			resultado.add(new AtaqueResponse(
					atacante.getId(),
					atacante.getClass().getSimpleName(),
					saludAntes,
					objetivo.getSalud()));
		}
		return resultado;
	}

	@Override
	public List<ComportamientoIA> listarComportamientos() {
		return entidadRepository.findAll().stream()
				.sorted(Comparator.comparing(Entidad::getId))
				.<ComportamientoIA>map(entidad -> new ComportamientoResponse(
						entidad.getClass().getSimpleName(),
						descripcion(entidad)))
				.toList();
	}

	private static int danioAtaque(Entidad atacante) {
		if (atacante instanceof Zombie) {
			return DANIO_ZOMBIE;
		}
		return 0;
	}

	private static String descripcion(Entidad entidad) {
		if (entidad instanceof Jugador) {
			return "Jugador construye objetos y ataca con la mano.";
		}
		if (entidad instanceof Creeper) {
			return "Creeper persigue y explota cerca del objetivo.";
		}
		return "Comportamiento no disponible.";
	}
}
