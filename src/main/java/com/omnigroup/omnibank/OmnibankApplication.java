package com.omnigroup.omnibank;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Omnibank Bank App",
                description = "Backend Rest APIs for Omnibank",
                version = "v1.0",
                contact = @Contact(
                        name = "Nathan Adebesin",
                        email = "nathanadebesin@gmail.com",
                        url = "https://github.com/omnizzy/omnibank"
                ),
                license = @License(
                        name = "Omnibank",
                        url = "https://github.com/omnizzy/omnibank"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "Omnibank Documentation",
                url = "https://github.com/omnizzy/omnibank"
        )
)
public class OmnibankApplication {

    public static void main(String[] args) {
        SpringApplication.run(OmnibankApplication.class, args);
    }

}
