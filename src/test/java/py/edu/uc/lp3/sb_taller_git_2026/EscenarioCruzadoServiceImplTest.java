package py.edu.uc.lp3.sb_taller_git_2026;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
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

import py.edu.uc.lp3.sb_taller_git_2026.domain.EscenarioCruzado;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Jugador;
import py.edu.uc.lp3.sb_taller_git_2026.domain.Vector;
import py.edu.uc.lp3.sb_taller_git_2026.domain.exception.ValidacionCompaneroException;
import py.edu.uc.lp3.sb_taller_git_2026.repository.EntidadRepository;
import py.edu.uc.lp3.sb_taller_git_2026.repository.EscenarioCruzadoRepository;
import py.edu.uc.lp3.sb_taller_git_2026.service.CompaneroClient;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.EscenarioCruzadoRequest;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.EscenarioCruzadoResponse;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ValidacionA2Request;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ValidacionA2Response;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ValidacionA3Request;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ValidacionA3Response;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ValidacionA3Response.Afectado;
import py.edu.uc.lp3.sb_taller_git_2026.service.impl.EscenarioCruzadoServiceImpl;

@ExtendWith(MockitoExtension.class)
class EscenarioCruzadoServiceImplTest {

	@Mock
	private EscenarioCruzadoRepository escenarioRepository;

	@Mock
	private EntidadRepository entidadRepository;

	@Mock
	private CompaneroClient companeroClient;

	@InjectMocks
	private EscenarioCruzadoServiceImpl service;

	@Test
	void guardaLaJugadaSoloSiA2YA3Aceptan() {
		prepararJugador();
		when(companeroClient.validarA2(new ValidacionA2Request("t2", "CerdoPrueba")))
				.thenReturn(new ValidacionA2Response(9L, "t2", "CerdoPrueba", 12, 4, 8));
		when(companeroClient.validarA3(eq(4L), any(ValidacionA3Request.class)))
				.thenReturn(new ValidacionA3Response(4L, 0,
						List.of(new Afectado(2L, "Player", 10, 10))));
		when(escenarioRepository.save(any(EscenarioCruzado.class))).thenAnswer(invocation -> {
			EscenarioCruzado guardado = invocation.getArgument(0);
			return new EscenarioCruzado(7L, guardado.getJugadorId(), guardado.getTntId(),
					guardado.getObjetivoA2Id(), guardado.getExplosionA2Id(), guardado.getDanoA2(),
					guardado.getSaludA2Despues(), guardado.getCreeperA3Id(), guardado.getObjetivoA3Id(),
					guardado.getDistanciaA3(), guardado.getDanoA3(), guardado.getVidaA3Despues());
		});

		EscenarioCruzadoResponse respuesta = service.ejecutar(request(4L));

		assertEquals(7L, respuesta.id());
		assertEquals("steve", respuesta.jugadorId());
		assertEquals(8, respuesta.danoA2());
		assertEquals(4, respuesta.saludA2Despues());
		assertEquals(10, respuesta.danoA3());
		assertEquals(10, respuesta.vidaA3Despues());
		verify(escenarioRepository).save(any(EscenarioCruzado.class));
	}

	@Test
	void rechazoDeA2NoPersisteNiLlamaAA3() {
		prepararJugador();
		when(companeroClient.validarA2(any(ValidacionA2Request.class)))
				.thenThrow(new ValidacionCompaneroException("A2", 409, "La TNT ya fue detonada"));

		assertThrows(ValidacionCompaneroException.class, () -> service.ejecutar(request(4L)));

		verify(companeroClient, never()).validarA3(anyLong(), any(ValidacionA3Request.class));
		verify(escenarioRepository, never()).save(any());
	}

	@Test
	void rechazoDeA3NoPersisteLaJugada() {
		prepararJugador();
		when(companeroClient.validarA2(any(ValidacionA2Request.class)))
				.thenReturn(new ValidacionA2Response(9L, "t2", "CerdoPrueba", 12, 4, 8));
		when(companeroClient.validarA3(eq(1L), any(ValidacionA3Request.class)))
				.thenThrow(new ValidacionCompaneroException("A3", 409, "El creeper ya explotó"));

		assertThrows(ValidacionCompaneroException.class, () -> service.ejecutar(request(1L)));

		verify(escenarioRepository, never()).save(any());
	}

	private void prepararJugador() {
		when(entidadRepository.findById("steve"))
				.thenReturn(Optional.of(new Jugador("steve", 20, 0, new Vector(3, 0, 0))));
	}

	private EscenarioCruzadoRequest request(Long creeperA3Id) {
		return new EscenarioCruzadoRequest("steve", "t2", "CerdoPrueba", creeperA3Id, 2L, 2);
	}
}
