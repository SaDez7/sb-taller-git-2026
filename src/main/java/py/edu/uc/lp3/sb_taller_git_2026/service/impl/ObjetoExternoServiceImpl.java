package py.edu.uc.lp3.sb_taller_git_2026.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import py.edu.uc.lp3.sb_taller_git_2026.service.ObjetoExternoService;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ObjetoExternoResponse;
import reactor.core.publisher.Mono;

@Service
public class ObjetoExternoServiceImpl implements ObjetoExternoService {

	private static final ParameterizedTypeReference<List<ObjetoExternoResponse>> TIPO_RESPUESTA =
			new ParameterizedTypeReference<>() {
			};

	private final WebClient webClient;

	public ObjetoExternoServiceImpl(
			WebClient.Builder builder,
			@Value("${api.restful-api.base-url}") String baseUrl) {
		this.webClient = builder.baseUrl(baseUrl).build();
	}

	@Override
	public Mono<List<ObjetoExternoResponse>> listar() {
		return webClient.get()
				.uri("/objects")
				.retrieve()
				.bodyToMono(TIPO_RESPUESTA);
	}
}
