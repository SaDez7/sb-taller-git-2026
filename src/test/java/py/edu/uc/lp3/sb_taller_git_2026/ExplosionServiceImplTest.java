package py.edu.uc.lp3.sb_taller_git_2026;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import py.edu.uc.lp3.sb_taller_git_2026.domain.Creeper;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Jugador;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Vector;
import py.edu.uc.lp3.sb_taller_git_2026.domain.exception.EntidadNoEncontradaException;
import py.edu.uc.lp3.sb_taller_git_2026.repository.BloqueRepository;
import py.edu.uc.lp3.sb_taller_git_2026.repository.EntidadRepository;
import py.edu.uc.lp3.sb_taller_git_2026.service.BloqueService;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ExplosionRequest;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ExplosionRequest.JugadorReferencia;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ExplosionResponse;
import py.edu.uc.lp3.sb_taller_git_2026.service.impl.ExplosionServiceImpl;

@ExtendWith(MockitoExtension.class)
class ExplosionServiceImplTest {

	@Mock
	private EntidadRepository entidadRepository;

	@Mock
	private BloqueRepository bloqueRepository;

	@Mock
	private BloqueService bloqueService;

	@InjectMocks
	private ExplosionServiceImpl service;

	@Test
	void laExplosionParteDeLaSaludPersistidaYAcumulaDanio() {
		Creeper creeper = new Creeper("creeper", 20);
		creeper.setPosicion(new Vector(0, 0, 0));
		Jugador steve = new Jugador("steve", 20, 0, new Vector(3, 0, 0));
		steve.setSalud(15);

		when(entidadRepository.findById("creeper")).thenReturn(Optional.of(creeper));
		when(entidadRepository.findById("steve")).thenReturn(Optional.of(steve));
		when(entidadRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		ExplosionRequest request = new ExplosionRequest(
				"creeper",
				5.0,
				List.of(new JugadorReferencia("steve")),
				List.of());

		ExplosionResponse primera = service.simular(request);
		assertEquals(15, primera.jugadores().get(0).saludAntes());
		assertEquals(8, primera.jugadores().get(0).danio());
		assertEquals(7, primera.jugadores().get(0).saludDespues());
		assertEquals(3.0, primera.jugadores().get(0).distancia(), 0.001);
		assertEquals(7, steve.getSalud());

		ExplosionResponse segunda = service.simular(request);
		assertEquals(7, segunda.jugadores().get(0).saludAntes());
		assertEquals(0, segunda.jugadores().get(0).saludDespues());
		assertEquals(0, steve.getSalud());
	}

	@Test
	void laExplosionNoCreaEntidadesAusentes() {
		when(entidadRepository.findById("creeper")).thenReturn(Optional.empty());

		ExplosionRequest request = new ExplosionRequest("creeper", 5.0, List.of(), List.of());

		assertThrows(EntidadNoEncontradaException.class, () -> service.simular(request));
		verify(entidadRepository, never()).save(any());
	}
}
