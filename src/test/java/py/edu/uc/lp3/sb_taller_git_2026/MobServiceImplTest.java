package py.edu.uc.lp3.sb_taller_git_2026;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import py.edu.uc.lp3.sb_taller_git_2026.domain.Creeper;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Jugador;
import py.edu.uc.lp3.sb_taller_git_2026.repository.EntidadRepository;
import py.edu.uc.lp3.sb_taller_git_2026.service.ComportamientoIA;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ComportamientoResponse;
import py.edu.uc.lp3.sb_taller_git_2026.service.impl.MobServiceImpl;

@ExtendWith(MockitoExtension.class)
class MobServiceImplTest {

	@Mock
	private EntidadRepository entidadRepository;

	@InjectMocks
	private MobServiceImpl service;

	@Test
	void listarComportamientosLeeEntidadesPersistidasYOrdenaPorId() {
		when(entidadRepository.findAll()).thenReturn(List.of(
				new Jugador("steve", 20),
				new Creeper("creeper", 20)));

		List<ComportamientoIA> comportamientos = service.listarComportamientos();

		assertEquals(2, comportamientos.size());
		assertInstanceOf(ComportamientoResponse.class, comportamientos.get(0));
		assertEquals("Creeper", comportamientos.get(0).tipo());
		assertEquals("Creeper persigue y explota cerca del objetivo.", comportamientos.get(0).descripcion());
		assertEquals("Jugador", comportamientos.get(1).tipo());
		assertEquals("Jugador construye objetos y ataca con la mano.", comportamientos.get(1).descripcion());
		verify(entidadRepository).findAll();
		verifyNoMoreInteractions(entidadRepository);
	}
}
