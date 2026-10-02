package com.ifgoianomih.contas_pagar.shared;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI cprOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API - Contas a Pagar e a Receber")
                .version("v1")
                .description("Projeto Integrador de Sistemas Distribuidos. "
                        + "Regras de negocio baseadas no livro Delphi 4 - Contas a Pagar e a Receber."));
    }
}
