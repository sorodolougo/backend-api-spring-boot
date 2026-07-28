// ====================================================================================
// ROLE : CONFIGURATION TRANSVERSE DE LA DOCUMENTATION (SWAGGER / OPENAPI)
// RESPONSIBILITY : Initialise et configure le moteur SpringDoc pour générer la page
// web interactive de l'API. Il centralise les métadonnées de la Gestion Commerciale
// (Titre, description de l'ERP, version de l'API et coordonnées du développeur).
// ARCHITECTURE : Agit comme une brique de plomberie globale. Elle permet de structurer
// la vitrine technique de l'application sans polluer le code des contrôleurs métiers.
// ====================================================================================

package com.jeanfrancis.backendapispringboot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GesCom ERP - API de Gestion Commerciale")
                        .version("1.0.0")
                        .description("Documentation interactive des endpoints REST du catalogue de stock.")
                        .contact(new Contact()
                                .name("Jean-Francis - Developpeur Senior Pro")
                                .email("contact@jeanfrancis.com")));
    }
}
