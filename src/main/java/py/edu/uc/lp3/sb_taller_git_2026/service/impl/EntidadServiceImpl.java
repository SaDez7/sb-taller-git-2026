package py.edu.uc.lp3.sb_taller_git_2026.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.sb_taller_git_2026.domain.Creeper;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Entidad;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Jugador;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Vector;
import py.edu.uc.lp3.sb_taller_git_2026.domain.exception.EntidadNoEncontradaException;
import py.edu.uc.lp3.sb_taller_git_2026.domain.exception.ReglaDeJuegoException;
import py.edu.uc.lp3.sb_taller_git_2026.repository.EntidadRepository;
import py.edu.uc.lp3.sb_taller_git_2026.service.EntidadService;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.EntidadRequest;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.EntidadResponse;

@Service
public class EntidadServiceImpl implements EntidadService {

	private static final String TIPO_JUGADOR = "jugador";
	private static final String TIPO_CREEPER = "creeper";

	private final EntidadRepository repository;

	public EntidadServiceImpl(EntidadRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<EntidadResponse> listar() {
		return repository.findAll().stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<EntidadResponse> listarVivos() {
		return repository.findBySaludGreaterThan(0).stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public EntidadResponse obtener(String id) {
		return toResponse(buscar(id));
	}

	@Override
	@Transactional
	public EntidadResponse crear(EntidadRequest request) {
		String tipo = normalizarTipo(request.tipo());
		String id = request.id() == null || request.id().isBlank()
				? UUID.randomUUID().toString()
				: request.id().trim();
		if (repository.existsById(id)) {
			throw new ReglaDeJuegoException("Ya existe una entidad con id " + id + ".");
		}

		int saludMaxima = validarSaludMaxima(request.saludMaxima());
		int salud = validarSalud(request.salud(), saludMaxima);
		int experiencia = validarExperiencia(request.experiencia());
		Vector posicion = crearVector(request.x(), request.y(), request.z());

		Entidad entidad;
		if (TIPO_JUGADOR.equals(tipo)) {
			Jugador jugador = new Jugador(id, saludMaxima, experiencia, posicion);
			jugador.actualizarEstado(salud, saludMaxima, posicion);
			entidad = jugador;
		} else {
			Creeper creeper = new Creeper(id, saludMaxima);
			creeper.actualizarEstado(salud, saludMaxima, posicion);
			entidad = creeper;
		}
		return toResponse(repository.save(entidad));
	}

	@Override
	@Transactional
	public EntidadResponse actualizar(String id, EntidadRequest request) {
		Entidad entidad = buscar(id);
		if (request.id() != null && !request.id().isBlank() && !id.equals(request.id().trim())) {
			throw new ReglaDeJuegoException("El id de la ruta no coincide con el id del cuerpo.");
		}
		if (request.tipo() != null && !request.tipo().isBlank()
				&& !normalizarTipo(request.tipo()).equals(tipoDe(entidad))) {
			throw new ReglaDeJuegoException("No se puede cambiar el tipo de una entidad existente.");
		}

		int saludMaxima = validarSaludMaxima(request.saludMaxima());
		int salud = validarSalud(request.salud(), saludMaxima);
		int experiencia = validarExperiencia(request.experiencia());
		Vector posicion = crearVector(request.x(), request.y(), request.z());

		entidad.actualizarEstado(salud, saludMaxima, posicion);
		if (entidad instanceof Jugador jugador) {
			jugador.setExperiencia(experiencia);
		}
		return toResponse(repository.save(entidad));
	}

	@Override
	@Transactional
	public void eliminar(String id) {
		Entidad entidad = buscar(id);
		repository.delete(entidad);
	}

	private Entidad buscar(String id) {
		return repository.findById(id)
				.orElseThrow(() -> new EntidadNoEncontradaException(id));
	}

	private EntidadResponse toResponse(Entidad entidad) {
		Vector posicion = entidad.getPosicion();
		int experiencia = entidad instanceof Jugador jugador ? jugador.getExperiencia() : 0;
		return new EntidadResponse(
				tipoDe(entidad),
				entidad.getId(),
				entidad.getSalud(),
				entidad.getSaludMaxima(),
				experiencia,
				posicion.getX(),
				posicion.getY(),
				posicion.getZ());
	}

	private String tipoDe(Entidad entidad) {
		if (entidad instanceof Jugador) {
			return TIPO_JUGADOR;
		}
		if (entidad instanceof Creeper) {
			return TIPO_CREEPER;
		}
		return entidad.getClass().getSimpleName().toLowerCase(Locale.ROOT);
	}

	private static String normalizarTipo(String tipo) {
		if (tipo == null || tipo.isBlank()) {
			throw new ReglaDeJuegoException("El tipo es obligatorio y debe ser jugador o creeper.");
		}
		String normalizado = tipo.trim().toLowerCase(Locale.ROOT);
		if (!TIPO_JUGADOR.equals(normalizado) && !TIPO_CREEPER.equals(normalizado)) {
			throw new ReglaDeJuegoException("Tipo de entidad invalido: " + tipo + ".");
		}
		return normalizado;
	}

	private static int validarSaludMaxima(Integer saludMaxima) {
		if (saludMaxima == null) {
			throw new ReglaDeJuegoException("La salud maxima es obligatoria.");
		}
		if (saludMaxima <= 0) {
			throw new ReglaDeJuegoException("La salud maxima debe ser mayor a 0.");
		}
		return saludMaxima;
	}

	private static int validarSalud(Integer salud, int saludMaxima) {
		if (salud == null) {
			throw new ReglaDeJuegoException("La salud es obligatoria.");
		}
		if (salud < 0) {
			throw new ReglaDeJuegoException("La salud no puede ser negativa.");
		}
		if (salud > saludMaxima) {
			throw new ReglaDeJuegoException("La salud no puede superar la salud maxima.");
		}
		return salud;
	}

	private static int validarExperiencia(Integer experiencia) {
		int valor = experiencia == null ? 0 : experiencia;
		if (valor < 0) {
			throw new ReglaDeJuegoException("La experiencia no puede ser negativa.");
		}
		return valor;
	}

	private static Vector crearVector(Double x, Double y, Double z) {
		double coordenadaX = coordenada(x, "x");
		double coordenadaY = coordenada(y, "y");
		double coordenadaZ = coordenada(z, "z");
		return new Vector(coordenadaX, coordenadaY, coordenadaZ);
	}

	private static double coordenada(Double valor, String nombre) {
		double coordenada = valor == null ? 0.0 : valor;
		if (!Double.isFinite(coordenada)) {
			throw new ReglaDeJuegoException("La coordenada " + nombre + " debe ser un numero finito.");
		}
		return coordenada;
	}
}
