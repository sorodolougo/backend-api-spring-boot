/**
 * ====================================================================================
 * ROLE : DATA TRANSFER OBJECT / INCOMING TRANSACTION SECURITY DTO
 * RESPONSIBILITY : Encapsule et valide de manière imperméable le panier d'achat
 * envoyé par ton application Angular avant qu'il ne pénètre le moteur transactionnel.
 * ARCHITECTURE : Utilise la validation en cascade (@Valid) pour sécuriser chaque ligne.
 * ====================================================================================
 */
package com.jeanfrancis.backendapispringboot.domain.invoice.dto;

import jakarta.validation.Valid; // 🟢 FIX CRITIQUE : Import indispensable pour la cascade
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class InvoiceRequestDto {

    @NotNull(message = "L'identifiant du client est obligatoire")
    private Long customerId;

    @NotEmpty(message = "Le panier d'achat ne peut pas être vide")
    @Valid // 🟢 FIX CRITIQUE : Force Spring Boot à descendre inspecter chaque ligne du panier d'achat
    private List<LineDto> lines;

    // ====================================================================================
    // GETTERS & SETTERS (MAIN PACK)
    // ====================================================================================
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public List<LineDto> getLines() { return lines; }
    public void setLines(List<LineDto> lines) { this.lines = lines; }

    // ====================================================================================
    // STRUCTURE INTERNE DE LIGNE ENCAPSULÉE
    // ====================================================================================
    public static class LineDto {

        @NotNull(message = "L'identifiant du produit est obligatoire")
        private Long productId;

        @NotNull(message = "La quantité est obligatoire")
        @Min(value = 1, message = "La quantité vendue doit être d'au moins 1 unité") // 🟢 SÉCURITÉ MÉTIER : Bloque les volumes à zéro ou négatifs
        private Integer quantity;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
