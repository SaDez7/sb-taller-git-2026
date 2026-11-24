package py.edu.uc.lp3.sb_taller_git_2026.service.impl;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import py.edu.uc.lp3.sb_taller_git_2026.domain.exception.CompaneroNoDisponibleException;
import py.edu.uc.lp3.sb_taller_git_2026.domain.exception.ValidacionCompaneroException;
import py.edu.uc.lp3.sb_taller_git_2026.service.CompaneroClient;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ValidacionA2Request;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ValidacionA2Response;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ValidacionA3Request;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ValidacionA3Response;
import reactor.core.publisher.Mono;

@Service
public class CompaneroWebClientImpl implements CompaneroClient {

	private static final Duration TIEMPO_ESPERA = Duration.ofSeconds(10);

	private final WebClient a2;
	private final WebClient a3;

	public CompaneroWebClientImpl(
			WebClient.Builder builder,
			@Value("${api.a2.base-url}") String baseUrlA2,
			@Value("${api.a3.base-url}") String baseUrlA3) {
		this.a2 = builder.clone().baseUrl(baseUrlA2).build();
		this.a3 = builder.clone().baseUrl(baseUrlA3).build();
	}

	@Override
	public ValidacionA2Response validarA2(ValidacionA2Request request) {
		Mono<ValidacionA2Response> respuesta = a2.post()
				.uri("/api/explosiones")
				.bodyValue(request)
				.retrieve()
				.bodyToMono(ValidacionA2Response.class);
		return esperar(respuesta, "A2");
	}

	@Override
	public ValidacionA3Response validarA3(Long creeperId, ValidacionA3Request request) {
		Mono<ValidacionA3Response> respuesta = a3.post()
				.uri("/entidades/{id}/explotar", creeperId)
				.bodyValue(request)
				.retrieve()
				.bodyToMono(ValidacionA3Response.class);
		return esperar(respuesta, "A3");
	}

	private <T> T esperar(Mono<T> respuesta, String api) {
		try {
			return respuesta.block(TIEMPO_ESPERA);
		} catch (WebClientResponseException error) {
			throw new ValidacionCompaneroException(api, error.getStatusCode().value(), error.getResponseBodyAsString());
		} catch (WebClientException | IllegalStateException error) {
			throw new CompaneroNoDisponibleException(api, error.getMessage());
		}
	}
}
