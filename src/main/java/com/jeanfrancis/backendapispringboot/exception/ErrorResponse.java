// ====================================================================================
// ROLE : DATA TRANSFER OBJECT (DTO) IMMUABLE DE RETOUR D'ERREUR
// RESPONSIBILITY : Modèle de structure de données sous forme de Record Java 21.
// Il standardise le format des erreurs envoyées au Front-end en fournissant un titre,
// un message compréhensible, le statut HTTP, et l'horodatage précis du plantage.
// ARCHITECTURE : Garantit une structure de réponse prédictible pour l'intercepteur Angular.
// ====================================================================================



package com.jeanfrancis.backendapispringboot.exception;

import java.time.LocalDateTime;

/**
 * STRUCTURE SÉCURISÉE DES ERREURS JSON (SENIOR PATTERN)
 * Ce moule immuable sera envoyé à Angular lors de chaque crash ou anomalie.
 */
public record ErrorResponse(
        String title,          // Titre de l'erreur (ex: "Ressource Introuvable", "Conflit de données")
        String message,        // Message explicatif en français pour l'utilisateur
        int status,            // Le code HTTP (400, 404, 500)
        LocalDateTime timestamp // L'heure exacte de l'anomalie pour les logs de production
) {}
