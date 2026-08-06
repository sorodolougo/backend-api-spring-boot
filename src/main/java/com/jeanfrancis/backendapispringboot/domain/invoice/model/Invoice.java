/**
 * ====================================================================================
 * ROLE : MODÈLE DE DONNÉES / ENTITÉ JPA MAÎTRESSE (INVOICE MASTER)
 * RESPONSIBILITY : Enregistre l'en-tête de facturation comptable, son statut de règlement
 * et orchestre la cascade de persistance SQL de l'ensemble de ses lignes de panier.
 * ARCHITECTURE : Jointure @ManyToOne vers Customer et @OneToMany vers InvoiceLine.
 * L'algorithme de ligne fusionne automatiquement les doublons d'articles en mémoire.
 * ====================================================================================
 */
package com.jeanfrancis.backendapispringboot.domain.invoice.model;

import com.jeanfrancis.backendapispringboot.domain.customer.model.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le numéro de facture est obligatoire")
    @Column(unique = true, nullable = false, length = 30)
    private String invoiceNumber; // Numéro séquentiel (ex: FAC-2026-0001)

    @NotNull(message = "Le client est obligatoire")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InvoiceLine> invoiceLines = new ArrayList<>();

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmountHt = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmountVat = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmountTtc = BigDecimal.ZERO;

    @NotBlank(message = "Le statut est obligatoire")
    @Column(nullable = false, length = 20)
    private String status = "BROUILLON"; // BROUILLON, VALIDEE, PAYEE, ANNULEE

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Helper Method Senior : Synchronise la relation bidirectionnelle,
     * fusionne intelligemment les lignes si le même article est scanné à nouveau,
     * et recalcule automatiquement les agrégations financières globales.
     */
    public void addLine(InvoiceLine line) {
        if (line == null || line.getProduct() == null) return;

        // 🧠 ALGORITHME DE FUSION DES LIGNES IDENTIQUES
        Optional<InvoiceLine> existingLineOpt = this.invoiceLines.stream()
                .filter(l -> l.getProduct().getId().equals(line.getProduct().getId()))
                .findFirst();

        if (existingLineOpt.isPresent()) {
            InvoiceLine existingLine = existingLineOpt.get();
            // On cumule simplement la nouvelle quantité sur l'ancienne ligne
            existingLine.setQuantity(existingLine.getQuantity() + line.getQuantity());
            existingLine.calculateTotals(); // Recalcul des montants propres à cette ligne fusionnée
        } else {
            // Nouvel article unique : On l'ajoute normalement à la collection
            this.invoiceLines.add(line);
            line.setInvoice(this);
            line.calculateTotals();
        }

        // RECALCUL TOTAL DE L'EN-TÊTE FINANCIER MASTER (RAZ PUIS RECALCUL PAR SÉCURITÉ COMPTABLE)
        recalculateGrandTotals();
    }

    /**
     * Méthode interne de consolidation financière pour éliminer les dérives d'arrondis.
     */
    private void recalculateGrandTotals() {
        this.totalAmountHt = BigDecimal.ZERO;
        this.totalAmountVat = BigDecimal.ZERO;
        this.totalAmountTtc = BigDecimal.ZERO;

        for (InvoiceLine line : this.invoiceLines) {
            this.totalAmountHt = this.totalAmountHt.add(line.getTotalHt());
            BigDecimal lineVat = line.getTotalTtc().subtract(line.getTotalHt());
            this.totalAmountVat = this.totalAmountVat.add(lineVat);
            this.totalAmountTtc = this.totalAmountTtc.add(line.getTotalTtc());
        }
    }

    // ====================================================================================
    // CONSTRUCTEURS (STANDARDS POJO)
    // ====================================================================================
    public Invoice() {}

    public Invoice(String invoiceNumber, Customer customer, String status) {
        this.invoiceNumber = invoiceNumber;
        this.customer = customer;
        this.status = status;
    }

    // ====================================================================================
    // GETTERS & SETTERS (ENCAPSULATION STRICTE)
    // ====================================================================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public List<InvoiceLine> getInvoiceLines() { return invoiceLines; }
    public void setInvoiceLines(List<InvoiceLine> invoiceLines) { this.invoiceLines = invoiceLines; }

    public BigDecimal getTotalAmountHt() { return totalAmountHt; }
    public void setTotalAmountHt(BigDecimal totalAmountHt) { this.totalAmountHt = totalAmountHt; }

    public BigDecimal getTotalAmountVat() { return totalAmountVat; }
    public void setTotalAmountVat(BigDecimal totalAmountVat) { this.totalAmountVat = totalAmountVat; }

    public BigDecimal getTotalAmountTtc() { return totalAmountTtc; }
    public void setTotalAmountTtc(BigDecimal totalAmountTtc) { this.totalAmountTtc = totalAmountTtc; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
