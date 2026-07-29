// ====================================================================================
// ROLE : CONFIGURATION TRANSVERSE DU SERVEUR WEB (PLOMBERIE COMPORTEMENTALE)
// RESPONSIBILITY : Centralise la sécurité de partage des ressources (CORS) à l'échelle
// globale de l'API. Elle autorise de manière sécurisée ton application Angular
// à exécuter des requêtes CRUD vers le serveur Spring Boot.
// ARCHITECTURE : Supprime définitivement le besoin de dupliquer l'annotation @CrossOrigin.
// ====================================================================================
package com.jeanfrancis.backendapispringboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * CENTRALISATION DU CORS (SENIOR PATTERN MULTI-ENVIRONNEMENT)
     * Autorise simultanément l'environnement de développement local et l'URL de production Vercel.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/**") // S'applique à tous les endpoints de l'API
                .allowedOrigins(
                        "http://localhost:4200",        // 🟢 Environnement de Développement Local (Sans le slash final)
                        "https://soro-gescom.vercel.app" // 🟢 Environnement de Production Cloud Vercel
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
