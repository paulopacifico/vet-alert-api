// @author Paulo Pacifico

package com.vetalert.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI vetAlertOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Vet Alert API")
                .version("1.0.0")
                .description("API secundaria que calcula e armazena o score de risco "
                        + "farmacologico a partir dos dados agregados da openFDA."));
    }
}
