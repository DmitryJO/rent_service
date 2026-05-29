package ru.dmsmirnov.rent.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI rentServiceOpenApi(
            @Value("${info.application.version:0.0.1-SNAPSHOT}") String version,
            @Value("${server.port:8080}") int serverPort) {
        return new OpenAPI()
                .info(new Info()
                        .title("Rent Service API")
                        .description("REST API сервиса аренды вещей")
                        .version(version)
                        .contact(new Contact()
                                .name("Rent Service Team")
                                .email("support@rent-service.local"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://rent-service.local/license")))
                .addServersItem(new Server()
                        .url("http://localhost:" + serverPort)
                        .description("Локальный сервер"));
    }

}
