package com.phatakp.ipl_services.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class OpenAPIConfig {

    private final AppProperties appProperties;

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl(appProperties.getUrl());
        server.setDescription("OpenAPI Documentation for IPL 2026 API");

        Contact contact = new Contact();
        contact.setName("Praveen Phatak");
        contact.setEmail("praveenphatakk@gmail.com");

        Info info = new Info()
                .title("IP2026 API")
                .description("IPL2026 API")
                .version("1.0")
                .contact(contact);
        return new OpenAPI().info(info).servers(List.of(server));
    }
}

