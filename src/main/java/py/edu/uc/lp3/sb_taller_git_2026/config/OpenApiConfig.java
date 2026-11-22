package py.edu.uc.lp3.sb_taller_git_2026.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("sb-taller-git-2026 API")
						.version("v1")
						.description("API REST del taller de Git: dominio Minecraft, persistencia H2, simulación y consulta externa con WebClient."));
	}
}
