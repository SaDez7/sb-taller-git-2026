package py.edu.uc.lp3.sb_taller_git_2026.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.sb_taller_git_2026.domain.Bloque;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Creeper;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Entidad;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Jugador;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Vector;
import py.edu.uc.lp3.sb_taller_git_2026.domain.exception.EntidadNoEncontradaException;
import py.edu.uc.lp3.sb_taller_git_2026.domain.exception.ReglaDeJuegoException;
import py.edu.uc.lp3.sb_taller_git_2026.repository.BloqueRepository;
import py.edu.uc.lp3.sb_taller_git_2026.repository.EntidadRepository;
import py.edu.uc.lp3.sb_taller_git_2026.service.ExplosionService;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ExplosionRequest;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ExplosionRequest.BloqueRequest;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ExplosionRequest.JugadorRequest;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ExplosionRequest.Posicion;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ExplosionResponse;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ExplosionResponse.BloqueAfectado;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ExplosionResponse.JugadorAfectado;

@Service
public class ExplosionServiceImpl implements ExplosionService {

	private static final int SALUD_CREEPER_POR_DEFECTO = 20;

	private final EntidadRepository entidadRepository;
	private final BloqueRepository bloqueRepository;

	public ExplosionServiceImpl(EntidadRepository entidadRepository, BloqueRepository bloqueRepository) {
		this.entidadRepository = entidadRepository;
		this.bloqueRepository = bloqueRepository;
	}

	@Override
	@Transactional
	public ExplosionResponse simular(ExplosionRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Los datos de la explosion no pueden ser null.");
		}

		String creeperId = validarTexto(request.creeperId(), "El id del creeper no puede estar vacio.");
		int saludMaximaCreeper = validarSaludMaximaCreeper(request.saludMaximaCreeper());
		double radio = validarRadio(request.radio());
		Vector origen = toVector(request.origen(), "El origen de la explosion");

		Creeper creeper = obtenerCreeper(creeperId, saludMaximaCreeper, origen);
		entidadRepository.save(creeper);

		List<JugadorRequest> jugadores = request.jugadores() == null ? List.of() : request.jugadores();
		List<BloqueRequest> bloques = request.bloques() == null ? List.of() : request.bloques();

		Set<String> ids = new HashSet<>();
		ids.add(creeperId);
		List<JugadorAfectado> jugadoresAfectados = new ArrayList<>();
		for (JugadorRequest jugadorRequest : jugadores) {
			jugadoresAfectados.add(simularJugador(creeper, radio, jugadorRequest, ids));
		}

		Set<Long> idsBloques = new HashSet<>();
		List<BloqueAfectado> bloquesAfectados = new ArrayList<>();
		for (BloqueRequest bloqueRequest : bloques) {
			bloquesAfectados.add(simularBloque(creeper, radio, bloqueRequest, idsBloques));
		}

		return new ExplosionResponse(creeper.getId(), radio, jugadoresAfectados, bloquesAfectados);
	}

	private JugadorAfectado simularJugador(Creeper creeper, double radio, JugadorRequest request, Set<String> ids) {
		if (request == null) {
			throw new IllegalArgumentException("Los datos de un jugador no pueden ser null.");
		}

		String id = validarTexto(request.id(), "El id del jugador no puede estar vacio.");
		if (!ids.add(id)) {
			throw new ReglaDeJuegoException("El id " + id + " esta repetido en la simulacion.");
		}
		int salud = validarSalud(request.salud(), "La salud del jugador " + id);
		int saludMaxima = validarSaludMaxima(request.saludMaxima(), "La salud maxima del jugador " + id);
		if (salud > saludMaxima) {
			throw new ReglaDeJuegoException("La salud del jugador " + id + " no puede superar su salud maxima.");
		}

		Vector posicion = toVector(request.posicion(), "La posicion del jugador " + id);
		Jugador jugador = obtenerJugador(id, salud, saludMaxima, posicion);
		int saludAntes = jugador.getSalud();
		double distancia = distancia(creeper.getPosicion(), jugador.getPosicion());
		int danio = creeper.danioDeExplosion(distancia, radio);
		if (danio > 0) {
			jugador.recibirDano(danio);
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

	private BloqueAfectado simularBloque(Creeper creeper, double radio, BloqueRequest request, Set<Long> ids) {
		if (request == null) {
			throw new IllegalArgumentException("Los datos de un bloque no pueden ser null.");
		}

		String tipo = validarTexto(request.tipo(), "El tipo de bloque no puede estar vacio.");
		Vector posicion = toVector(request.posicion(), "La posicion del bloque " + tipo);
		if (request.id() != null && !ids.add(request.id())) {
			throw new ReglaDeJuegoException("El id de bloque " + request.id() + " esta repetido.");
		}
		Bloque bloque = obtenerBloque(request.id(), tipo, posicion);

		double distancia = distancia(creeper.getPosicion(), bloque.getPosicion());
		boolean destruido = bloque.destruidoPorExplosion(distancia, radio);
		bloqueRepository.save(bloque);

		return new BloqueAfectado(bloque.getId(), bloque.getTipo(), redondear(distancia), destruido);
	}

	private Creeper obtenerCreeper(String id, int saludMaxima, Vector origen) {
		return entidadRepository.findById(id)
				.map(entidad -> {
					if (!(entidad instanceof Creeper creeper)) {
						throw new ReglaDeJuegoException("La entidad " + id + " no es un creeper.");
					}
					creeper.actualizarEstado(saludMaxima, saludMaxima, origen);
					return creeper;
				})
				.orElseGet(() -> {
					Creeper creeper = new Creeper(id, saludMaxima);
					creeper.actualizarEstado(saludMaxima, saludMaxima, origen);
					return creeper;
				});
	}

	private Jugador obtenerJugador(String id, int salud, int saludMaxima, Vector posicion) {
		return entidadRepository.findById(id)
				.map(entidad -> {
					if (!(entidad instanceof Jugador jugador)) {
						throw new ReglaDeJuegoException("La entidad " + id + " no es un jugador.");
					}
					jugador.actualizarEstado(salud, saludMaxima, posicion);
					return jugador;
				})
				.orElseGet(() -> {
					Jugador jugador = new Jugador(id, saludMaxima, 0, posicion);
					jugador.actualizarEstado(salud, saludMaxima, posicion);
					return jugador;
				});
	}

	private Bloque obtenerBloque(Long id, String tipo, Vector posicion) {
		if (id == null) {
			return new Bloque(tipo, posicion);
		}
		return bloqueRepository.findById(id)
				.map(bloque -> {
					bloque.setTipo(tipo);
					bloque.setPosicion(posicion);
					return bloque;
				})
				.orElseThrow(() -> new EntidadNoEncontradaException("bloque " + id));
	}

	private static String validarTexto(String valor, String mensaje) {
		if (valor == null || valor.isBlank()) {
			throw new IllegalArgumentException(mensaje);
		}
		return valor.trim();
	}

	private static double validarRadio(Double radio) {
		if (radio == null) {
			throw new IllegalArgumentException("El radio de la explosion es obligatorio.");
		}
		if (!Double.isFinite(radio) || radio <= 0) {
			throw new ReglaDeJuegoException("El radio de la explosion debe ser mayor a 0.");
		}
		return radio;
	}

	private static int validarSalud(Integer salud, String descripcion) {
		if (salud == null) {
			throw new IllegalArgumentException(descripcion + " es obligatoria.");
		}
		if (salud < 0) {
			throw new ReglaDeJuegoException(descripcion + " no puede ser negativa.");
		}
		return salud;
	}

	private static int validarSaludMaxima(Integer saludMaxima, String descripcion) {
		if (saludMaxima == null) {
			throw new IllegalArgumentException(descripcion + " es obligatoria.");
		}
		if (saludMaxima <= 0) {
			throw new ReglaDeJuegoException(descripcion + " debe ser mayor a 0.");
		}
		return saludMaxima;
	}

	private static int validarSaludMaximaCreeper(Integer saludMaxima) {
		if (saludMaxima == null) {
			return SALUD_CREEPER_POR_DEFECTO;
		}
		return validarSaludMaxima(saludMaxima, "La salud maxima del creeper");
	}

	private static Vector toVector(Posicion posicion, String descripcion) {
		if (posicion == null) {
			throw new IllegalArgumentException(descripcion + " no puede ser null.");
		}
		if (posicion.x() == null || posicion.y() == null || posicion.z() == null) {
			throw new IllegalArgumentException(descripcion + " debe tener las coordenadas x, y y z.");
		}
		if (!Double.isFinite(posicion.x()) || !Double.isFinite(posicion.y()) || !Double.isFinite(posicion.z())) {
			throw new IllegalArgumentException(descripcion + " tiene coordenadas invalidas.");
		}
		return new Vector(posicion.x(), posicion.y(), posicion.z());
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
