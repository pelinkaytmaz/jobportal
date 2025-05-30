package com.dauphine.jobportal.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Job Portal API")
                .version("1.0.0")
                .description("API REST pour le portail d'emploi - Développé par Pelin Kaytmaz et Emma Nafaa")
                .contact(new Contact()
                    .name("Pelin Kaytmaz & Emma Nafaa")
                    .email("p.kaytmaz@gmail.com")))
            .servers(Arrays.asList(
                // new Server()
                //     .url("https://api.dauphine-jobportal.com")
                //     .description("Production"),
                // new Server()
                //     .url("https://staging-api.dauphine-jobportal.com")
                //     .description("Staging"),
                new Server()
                    .url("http://localhost:8080")
                    .description("Développement")
            ))
            .addSecurityItem(new SecurityRequirement().addList("JWT"))
            .components(new Components()
                .addSecuritySchemes("JWT", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Token JWT obtenu via l'endpoint /auth/login")));
    }
}