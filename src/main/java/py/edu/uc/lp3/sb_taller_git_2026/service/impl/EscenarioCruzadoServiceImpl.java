package py.edu.uc.lp3.sb_taller_git_2026.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.sb_taller_git_2026.domain.Entidad;
import py.edu.uc.lp3.sb_taller_git_2026.domain.EscenarioCruzado;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Jugador;
import py.edu.uc.lp3.sb_taller_git_2026.domain.exception.EntidadNoEncontradaException;
import py.edu.uc.lp3.sb_taller_git_2026.domain.exception.ReglaDeJuegoException;
import py.edu.uc.lp3.sb_taller_git_2026.domain.exception.ValidacionCompaneroException;
import py.edu.uc.lp3.sb_taller_git_2026.repository.EntidadRepository;
import py.edu.uc.lp3.sb_taller_git_2026.repository.EscenarioCruzadoRepository;
import py.edu.uc.lp3.sb_taller_git_2026.service.CompaneroClient;
import py.edu.uc.lp3.sb_taller_git_2026.service.EscenarioCruzadoService;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.EscenarioCruzadoRequest;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.EscenarioCruzadoResponse;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ValidacionA2Request;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ValidacionA2Response;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ValidacionA3Request;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ValidacionA3Response;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ValidacionA3Response.Afectado;

@Service
public class EscenarioCruzadoServiceImpl implements EscenarioCruzadoService {

	private static final int DISTANCIA_MAXIMA_A3 = 3;

	private final EscenarioCruzadoRepository escenarioRepository;
	private final EntidadRepository entidadRepository;
	private final CompaneroClient companeroClient;

	public EscenarioCruzadoServiceImpl(
			EscenarioCruzadoRepository escenarioRepository,
			EntidadRepository entidadRepository,
			CompaneroClient companeroClient) {
		this.escenarioRepository = escenarioRepository;
		this.entidadRepository = entidadRepository;
		this.companeroClient = companeroClient;
	}

	@Override
	@Transactional
	public EscenarioCruzadoResponse ejecutar(EscenarioCruzadoRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Los datos del escenario no pueden ser null.");
		}

		Jugador jugador = obtenerJugadorVivo(request.jugadorId());
		String jugadorId = jugador.getId();
		String tntId = validarTexto(request.tntId(), "El id de la TNT de A2 no puede estar vacío.");
		String objetivoA2Id = validarTexto(request.objetivoA2Id(), "El id del objetivo de A2 no puede estar vacío.");
		validarA3(request);

		ValidacionA2Response a2 = companeroClient.validarA2(new ValidacionA2Request(tntId, objetivoA2Id));
		ValidacionA3Request a3Request = new ValidacionA3Request(
				List.of(new ValidacionA3Request.Objetivo(request.objetivoA3Id(), request.distanciaA3())));
		ValidacionA3Response a3 = companeroClient.validarA3(request.creeperA3Id(), a3Request);
		Afectado afectado = obtenerAfectado(a3, request.objetivoA3Id());

		EscenarioCruzado escenario = escenarioRepository.save(new EscenarioCruzado(
				jugadorId,
				tntId,
				objetivoA2Id,
				a2.id(),
				a2.danoAplicado(),
				a2.saludActual(),
				a3.creeperId(),
				afectado.id(),
				request.distanciaA3(),
				afectado.danoRecibido(),
				afectado.vidaFinal()));
		return mapear(escenario);
	}

	@Override
	public List<EscenarioCruzadoResponse> listar() {
		return escenarioRepository.findAll().stream()
				.map(this::mapear)
				.toList();
	}

	@Override
	public EscenarioCruzadoResponse obtener(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("El id del escenario es obligatorio.");
		}
		return escenarioRepository.findById(id)
				.map(this::mapear)
				.orElseThrow(() -> new EntidadNoEncontradaException("escenario cruzado " + id));
	}

	private Jugador obtenerJugadorVivo(String id) {
		String jugadorId = validarTexto(id, "El id del jugador no puede estar vacío.");
		Entidad entidad = entidadRepository.findById(jugadorId)
				.orElseThrow(() -> new EntidadNoEncontradaException("jugador " + jugadorId));
		if (!(entidad instanceof Jugador jugador)) {
			throw new ReglaDeJuegoException("La entidad " + jugadorId + " no es un jugador.");
		}
		if (jugador.getSalud() <= 0) {
			throw new ReglaDeJuegoException("El jugador " + jugadorId + " no está vivo.");
		}
		return jugador;
	}

	private void validarA3(EscenarioCruzadoRequest request) {
		if (request.creeperA3Id() == null) {
			throw new IllegalArgumentException("El id del creeper de A3 es obligatorio.");
		}
		if (request.creeperA3Id() <= 0) {
			throw new IllegalArgumentException("El id del creeper de A3 debe ser positivo.");
		}
		if (request.objetivoA3Id() == null) {
			throw new IllegalArgumentException("El id del objetivo de A3 es obligatorio.");
		}
		if (request.objetivoA3Id() <= 0) {
			throw new IllegalArgumentException("El id del objetivo de A3 debe ser positivo.");
		}
		if (request.distanciaA3() < 0 || request.distanciaA3() > DISTANCIA_MAXIMA_A3) {
			throw new ReglaDeJuegoException("La distancia en A3 debe estar entre 0 y 3 bloques.");
		}
	}

	private Afectado obtenerAfectado(ValidacionA3Response a3, Long objetivoId) {
		if (a3 == null || a3.afectados() == null) {
			throw new ValidacionCompaneroException("A3", 200, "La respuesta de A3 no incluyó afectados.");
		}
		return a3.afectados().stream()
				.filter(afectado -> objetivoId.equals(afectado.id()))
				.findFirst()
				.orElseThrow(() -> new ValidacionCompaneroException("A3", 200,
						"La respuesta de A3 no incluyó el objetivo " + objetivoId + "."));
	}

	private EscenarioCruzadoResponse mapear(EscenarioCruzado escenario) {
		return new EscenarioCruzadoResponse(
				escenario.getId(),
				escenario.getJugadorId(),
				escenario.getTntId(),
				escenario.getObjetivoA2Id(),
				escenario.getExplosionA2Id(),
				escenario.getDanoA2(),
				escenario.getSaludA2Despues(),
				escenario.getCreeperA3Id(),
				escenario.getObjetivoA3Id(),
				escenario.getDistanciaA3(),
				escenario.getDanoA3(),
				escenario.getVidaA3Despues());
	}

	private static String validarTexto(String valor, String mensaje) {
		if (valor == null || valor.isBlank()) {
			throw new IllegalArgumentException(mensaje);
		}
		return valor.trim();
	}
}
