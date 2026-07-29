// ====================================================================================
// ROLE : CONFIGURATION TRANSVERSE DU SERVEUR WEB (PLOMBERIE COMPORTEMENTALE)
// RESPONSIBILITY : Centralise la sécurité de partage des ressources (CORS) à l'échelle
// globale de l'API. Elle autorise de manière sécurisée ton application Angular
// (localhost:4200) à exécuter des requêtes CRUD vers le serveur Spring Boot (localhost:8080).
// ARCHITECTURE : Supprime définitivement le besoin de dupliquer l'annotation @CrossOrigin.
// ====================================================================================



package com.jeanfrancis.backendapispringboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * CENTRALISATION DU CORS (SENIOR PATTERN)
     * Automatise l'autorisation de communication entre Angular (Port 4200) et Spring Boot (Port 8080)
     * pour l'ensemble des modules présents et futurs de la GesCom ERP.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/**") // S'applique à tous les endpoints de l'API
                .allowedOrigins("https://soro-gescom.vercel.app") // Autorise uniquement ton serveur Angular
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Autorise tous les verbes CRUD
                .allowedHeaders("*") // Autorise tous les types de headers HTTP (Authorization, Content-Type, etc.)
                .allowCredentials(true) // Crucial si tu ajoutes plus tard la gestion des cookies/sessions HTTP
                .maxAge(3600); // Met en cache la pré-requête OPTIONS pendant 1 heure pour optimiser les performances réseau
    }
}


