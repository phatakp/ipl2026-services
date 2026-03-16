package com.phatakp.ipl_services;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        tags = {
                @Tag(name="User Module", description = "Operations with users data"),
                @Tag(name="Teams Module", description = "Operations with IPL teams data"),
                @Tag(name="Matches Module", description = "Operations with IPL matches data"),
                @Tag(name="Matches Admin Module", description = "Admin Operations with IPL matches data"),
                @Tag(name="Predictions Module", description = "Operations with IPL predictions data"),
        }
)

@SpringBootApplication
public class IplServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(IplServicesApplication.class, args);
	}

}
