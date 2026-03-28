package com.emastery.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI emasteryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-Mastery API")
                        .description("API cho hệ thống học từ vựng dùng AI")
                        .version("1.0"));
    }
}