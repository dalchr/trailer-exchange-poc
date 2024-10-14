package org.trailerexchange.endpoints.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
/*import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;*/
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition()
public class ApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Transport Order Application API (Standalone runtime)")
                .version("1.0")
                .description("This is the API documentation for the application."))
            // .addServersItem(new Server().url(clientUrl));
            .addServersItem(new Server().url("http://localhost:8080"));
    }
}
