// ====================================================================================
// ROLE : GESTIONNAIRE D'EXCEPTIONS INTERNE GLOBAL (LE FILET DE SÉCURITÉ REST)
// RESPONSIBILITY : Intercepte automatiquement tous les crashs ou exceptions levés par
// n'importe quel domaine (Stock, Client, etc.) avant qu'ils ne quittent le serveur.
// Il transforme une erreur Java brute en un objet JSON ErrorResponse propre et sécurisé.
// ARCHITECTURE : Aligne les codes HTTP réels (400, 404, 500) pour l'intercepteur d'Angular.
// ====================================================================================



package com.jeanfrancis.backendapispringboot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice // Indique à Spring que cette classe intercepte globalement les erreurs de TOUS les contrôleurs
public class GlobalExceptionHandler {

    /**
     * INTERCEPTION 1 : ERREUR DE VALIDATION OU MAUVAIS ARGUMENT (HTTP 400)
     * Déclenchée par exemple si le SKU existe déjà ou si une règle métier est violée.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
                "Requête Invalide",
                ex.getMessage(), // Récupère le message précis défini dans ton service
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * INTERCEPTION 2 : RESSOURCE NON TROUVÉE (HTTP 404)
     * Déclenchée si un utilisateur demande un produit ou un client qui n'existe pas via son ID.
     */
    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(jakarta.persistence.EntityNotFoundException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
                "Ressource Introuvable",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * INTERCEPTION 3 : LE FILET DE SÉCURITÉ ULTIME (HTTP 500)
     * Attrape toutes les autres pannes imprévues (Erreur SQL brute, NullPointerException, etc.)
     * Évite de divulguer du code Java sensible dans le navigateur du client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
                "Erreur Interne Serveur",
                "Une panne technique est survenue sur le serveur de l'ERP. Veuillez contacter l'administrateur.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );

        // Trame de log indispensable pour le développeur dans la console Spring Boot
        System.err.println("[CRITICAL ERROR LOG] : " + ex.getMessage());
        ex.printStackTrace();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
