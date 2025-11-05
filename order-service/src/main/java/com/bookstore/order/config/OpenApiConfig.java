package com.bookstore.order.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Value("${swagger.server.url:http://localhost:8083}")
    private String swaggerServerUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bookstore Order Service API")
                        .version("1.0")
                        .description("Handles book order creation, retrieval and cancellation.")
                        .contact(new Contact()
                                .name("Kenzo de Albuquerque")
                                .email("kenzoalbuqk@gmail.com")
                                .url("https://github.com/OzneKx")
                        )
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")
                        )
                )
                .servers(List.of(
                        new Server()
                                .url(swaggerServerUrl)
                                .description("Local or container environment")
                ));
    }
}
