package com.api_orcafacil.config;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration(proxyBeanMethods = false)
public class SwaggerConfig {

    @Bean
    OpenAPI customOpenAPI(@Value("${server.servlet.context-path:}") String contextPath) {
        final String securitySchemeName = "bearerAuth";
        String basePath = contextPath == null || contextPath.isBlank() ? "" : contextPath;

        return new OpenAPI()
                .info(new Info()
                        .title("API OrcaFacil")
                        .version("v1")
                        .description("API SaaS de orcamentos com autenticacao JWT"))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .servers(List.of(new Server().url("http://localhost:8080" + basePath)));
    }

    @Bean
    GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("api")
                .pathsToMatch("/**")
                .build();
    }
}
