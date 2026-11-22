package py.edu.uc.lp3.sb_taller_git_2026.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.sb_taller_git_2026.domain.Bloque;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Creeper;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Jugador;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Vector;
import py.edu.uc.lp3.sb_taller_git_2026.domain.exception.EntidadNoEncontradaException;
import py.edu.uc.lp3.sb_taller_git_2026.domain.exception.ReglaDeJuegoException;
import py.edu.uc.lp3.sb_taller_git_2026.repository.BloqueRepository;
import py.edu.uc.lp3.sb_taller_git_2026.repository.EntidadRepository;
import py.edu.uc.lp3.sb_taller_git_2026.service.BloqueService;
import py.edu.uc.lp3.sb_taller_git_2026.service.ExplosionService;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ExplosionRequest;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ExplosionRequest.BloqueReferencia;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ExplosionRequest.JugadorReferencia;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ExplosionResponse;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ExplosionResponse.BloqueAfectado;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ExplosionResponse.JugadorAfectado;

@Service
public class ExplosionServiceImpl implements ExplosionService {

	private static final int DANIO_MAXIMO_CREEPER = 20;

	private final EntidadRepository entidadRepository;
	private final BloqueRepository bloqueRepository;
	private final BloqueService bloqueService;

	public ExplosionServiceImpl(EntidadRepository entidadRepository, BloqueRepository bloqueRepository,
			BloqueService bloqueService) {
		this.entidadRepository = entidadRepository;
		this.bloqueRepository = bloqueRepository;
		this.bloqueService = bloqueService;
	}

	@Override
	@Transactional
	public ExplosionResponse simular(ExplosionRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Los datos de la explosión no pueden ser null.");
		}

		String creeperId = validarTexto(request.creeperId(), "El id del creeper no puede estar vacío.");
		double radio = validarRadio(request.radio());
		Creeper creeper = obtenerCreeper(creeperId);

		List<JugadorReferencia> jugadores = request.jugadores() == null ? List.of() : request.jugadores();
		List<BloqueReferencia> bloques = request.bloques() == null ? List.of() : request.bloques();

		Set<String> ids = new HashSet<>();
		ids.add(creeperId);
		List<JugadorAfectado> jugadoresAfectados = new ArrayList<>();
		for (JugadorReferencia jugadorReferencia : jugadores) {
			jugadoresAfectados.add(simularJugador(creeper, radio, jugadorReferencia, ids));
		}

		Set<Long> idsBloques = new HashSet<>();
		List<BloqueAfectado> bloquesAfectados = new ArrayList<>();
		for (BloqueReferencia bloqueReferencia : bloques) {
			bloquesAfectados.add(simularBloque(creeper, radio, bloqueReferencia, idsBloques));
		}

		return new ExplosionResponse(creeper.getId(), radio, jugadoresAfectados, bloquesAfectados);
	}

	private JugadorAfectado simularJugador(Creeper creeper, double radio, JugadorReferencia request,
			Set<String> ids) {
		if (request == null) {
			throw new IllegalArgumentException("Los datos de un jugador no pueden ser null.");
		}

		String id = validarTexto(request.id(), "El id del jugador no puede estar vacío.");
		if (!ids.add(id)) {
			throw new ReglaDeJuegoException("El id " + id + " está repetido en la simulación.");
		}

		Jugador jugador = obtenerJugador(id);
		int saludAntes = jugador.getSalud();
		double distancia = distancia(creeper.getPosicion(), jugador.getPosicion());
		int danio = danioDeExplosion(distancia, radio);
		if (danio > 0) {
			jugador.setSalud(Math.max(0, jugador.getSalud() - danio));
		}
		entidadRepository.save(jugador);

		return new JugadorAfectado(
				jugador.getId(),
				redondear(distancia),
				danio > 0,
				saludAntes - jugador.getSalud(),
				saludAntes,
				jugador.getSalud());
	}

	private BloqueAfectado simularBloque(Creeper creeper, double radio, BloqueReferencia request, Set<Long> ids) {
		if (request == null) {
			throw new IllegalArgumentException("Los datos de un bloque no pueden ser null.");
		}
		if (request.id() == null) {
			throw new IllegalArgumentException("El id del bloque es obligatorio.");
		}
		if (!ids.add(request.id())) {
			throw new ReglaDeJuegoException("El id de bloque " + request.id() + " está repetido.");
		}

		Bloque bloque = obtenerBloque(request.id());
		double distancia = distancia(creeper.getPosicion(), bloque.getPosicion());
		Bloque afectado = bloqueService.aplicarExplosion(bloque, distancia, radio);
		bloqueRepository.save(afectado);

		return new BloqueAfectado(afectado.getId(), afectado.getTipo(), redondear(distancia), afectado.isDestruido());
	}

	private Creeper obtenerCreeper(String id) {
		return entidadRepository.findById(id)
				.map(entidad -> {
					if (!(entidad instanceof Creeper creeper)) {
						throw new ReglaDeJuegoException("La entidad " + id + " no es un creeper.");
					}
					return creeper;
				})
				.orElseThrow(() -> new EntidadNoEncontradaException("creeper " + id));
	}

	private Jugador obtenerJugador(String id) {
		return entidadRepository.findById(id)
				.map(entidad -> {
					if (!(entidad instanceof Jugador jugador)) {
						throw new ReglaDeJuegoException("La entidad " + id + " no es un jugador.");
					}
					return jugador;
				})
				.orElseThrow(() -> new EntidadNoEncontradaException("jugador " + id));
	}

	private Bloque obtenerBloque(Long id) {
		return bloqueRepository.findById(id)
				.orElseThrow(() -> new EntidadNoEncontradaException("bloque " + id));
	}

	private static int danioDeExplosion(double distancia, double radio) {
		validarRadio(radio);
		if (distancia < 0) {
			throw new ReglaDeJuegoException("La distancia a la explosión no puede ser negativa.");
		}
		if (distancia > radio) {
			return 0;
		}
		double factor = 1.0 - (distancia / radio);
		return Math.max(1, (int) Math.round(DANIO_MAXIMO_CREEPER * factor));
	}

	private static String validarTexto(String valor, String mensaje) {
		if (valor == null || valor.isBlank()) {
			throw new IllegalArgumentException(mensaje);
		}
		return valor.trim();
	}

	private static double validarRadio(Double radio) {
		if (radio == null) {
			throw new IllegalArgumentException("El radio de la explosión es obligatorio.");
		}
		if (!Double.isFinite(radio) || radio <= 0) {
			throw new ReglaDeJuegoException("El radio de la explosión debe ser mayor a 0.");
		}
		return radio;
	}

	private static double distancia(Vector origen, Vector destino) {
		double dx = origen.getX() - destino.getX();
		double dy = origen.getY() - destino.getY();
		double dz = origen.getZ() - destino.getZ();
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	private static double redondear(double valor) {
		return Math.round(valor * 100.0) / 100.0;
	}
}
