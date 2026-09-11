package vanguard.web;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Customizes the auto-generated Swagger/OpenAPI docs with a proper
 * title and description instead of springdoc's generic defaults.
 * Nothing else needed — springdoc-openapi reads the @RestController
 * classes in the web package automatically and builds the docs from
 * their method signatures.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI vanguardOpenApi() {
        return new OpenAPI().info(new Info()
                .title("VANGUARD API")
                .description("AI-style disaster response and resource management system — "
                        + "REST API for reporting incidents, running allocation, and managing "
                        + "resources. Shares its backend with the console and Swing GUI versions "
                        + "of the same project.")
                .version("1.0.0"));
    }
}
