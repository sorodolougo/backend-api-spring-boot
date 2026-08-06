/**
 * ====================================================================================
 * ROLE : MODÈLE DE DONNÉES / ENTITÉ JPA (LIGNE DE FACTURE DÉTAILLÉE)
 * RESPONSIBILITY : Cartographie chaque article lié au panier d'achat d'une transaction.
 * Conserve l'historique et la photographie financière immuable de la TVA vendue.
 * ARCHITECTURE : Relation ManyToOne inversée vers le Master Invoice.
 * ====================================================================================
 */
package com.jeanfrancis.backendapispringboot.domain.invoice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jeanfrancis.backendapispringboot.domain.stock.model.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "invoice_lines")
public class InvoiceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    @JsonIgnore // Empêche les boucles infinies de sérialisation Jackson JSON
    private Invoice invoice;

    @NotNull(message = "Le produit est obligatoire")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull(message = "La quantité est obligatoire")
    @Column(nullable = false)
    private Integer quantity;

    @NotNull(message = "Le prix unitaire HT est obligatoire")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPriceHt;

    @NotNull(message = "Le taux de TVA est obligatoire")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal vatRate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalHt;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalVat; // 🟢 FIX SENIOR : Colonne physique ajoutée pour l'audit fiscal direct

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalTtc;

    // ====================================================================================
    // DECLENCHEURS CYCLE DE VIE (CALCULS COMPTABLES STRICTS AVANT PERSISTANCE)
    // ====================================================================================
    @PrePersist
    @PreUpdate
    public void calculateTotals() {
        BigDecimal qty = BigDecimal.valueOf(this.quantity);

        // 1. Calcul du montant de base Hors Taxes
        this.totalHt = this.unitPriceHt.multiply(qty);

        // 2. Calcul du montant exact de la TVA (avec division sécurisée sur 4 décimales pour éviter les dérives d'arrondis)
        this.totalVat = this.totalHt.multiply(this.vatRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // 3. Calcul du Net à Payer Toutes Taxes Comprises
        this.totalTtc = this.totalHt.add(this.totalVat);
    }

    // ====================================================================================
    // CONSTRUCTEURS (STANDARDS POJO)
    // ====================================================================================
    public InvoiceLine() {}

    public InvoiceLine(Product product, Integer quantity, BigDecimal unitPriceHt, BigDecimal vatRate) {
        this.product = product;
        this.quantity = quantity;
        this.unitPriceHt = unitPriceHt;
        this.vatRate = vatRate;
    }

    // ====================================================================================
    // GETTERS & SETTERS (ENCAPSULATION STRICTE)
    // ====================================================================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPriceHt() { return unitPriceHt; }
    public void setUnitPriceHt(BigDecimal unitPriceHt) { this.unitPriceHt = unitPriceHt; }

    public BigDecimal getVatRate() { return vatRate; }
    public void setVatRate(BigDecimal vatRate) { this.vatRate = vatRate; }

    public BigDecimal getTotalHt() { return totalHt; }
    public void setTotalHt(BigDecimal totalHt) { this.totalHt = totalHt; }

    public BigDecimal getTotalVat() { return totalVat; }
    public void setTotalVat(BigDecimal totalVat) { this.totalVat = totalVat; }

    public BigDecimal getTotalTtc() { return totalTtc; }
    public void setTotalTtc(BigDecimal totalTtc) { this.totalTtc = totalTtc; }
}
