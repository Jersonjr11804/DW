package com.prueba3.pagina3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pagina Pizza API")
                        .version("1.0")
                        .description("Documentación OpenAPI de PaginaPizza"))
                .addServersItem(new Server().url("http://localhost:8080").description("Local server"));
    }
}
