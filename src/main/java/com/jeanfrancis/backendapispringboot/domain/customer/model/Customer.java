/**
 * ====================================================================================
 * ROLE : MODÈLE DE DONNÉES / ENTITÉ JPA (DOMAINE CLIENT)
 * RESPONSIBILITY : Cartographie la structure de la table "customers" dans PostgreSQL.
 * Elle encapsulate l'identité et les coordonnées d'un tiers (Quincaillerie, Supermarché...).
 * ARCHITECTURE : Utilise les validations Jakarta pour garantir l'intégrité avant écriture.
 * ====================================================================================
 */


package com.jeanfrancis.backendapispringboot.domain.customer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le code client est obligatoire")
    @Column(unique = true, nullable = false, length = 20)
    private String customerCode; // Codification d'entreprise unique (ex: CLI-2026-001)

    @NotBlank(message = "La raison sociale ou le nom est obligatoire")
    @Column(nullable = false, length = 100)
    private String name; // Nom du client ou raison sociale de la PME (Quincaillerie, Supermarché, etc.)

    @Column(length = 20)
    private String phone;

    @Email(message = "L'adresse email doit être valide")
    @Column(length = 100)
    private String email;

    @Column(length = 255)
    private String address;

    @NotNull(message = "Le type de client est obligatoire")
    @Column(nullable = false, length = 20)
    private String customerType; // "PARTICULIER" ou "ENTREPRISE" (B2B)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ====================================================================================
    // DECLENCHEURS CYCLE DE VIE (JPA LIFECYCLE HOOKS)
    // Autopilote : Assure la traçabilité temporelle de la fiche client lors de l'insertion
    // ====================================================================================
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ====================================================================================
    // CONSTRUCTEURS (STANDARDS POJO)
    // ====================================================================================
    public Customer() {
    }

    public Customer(String customerCode, String name, String phone, String email, String address, String customerType) {
        this.customerCode = customerCode;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.customerType = customerType;
    }

    // ====================================================================================
    // GETTERS & SETTERS (ENCAPSULATION STRICTE)
    // ====================================================================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCustomerType() { return customerType; }
    public void setCustomerType(String customerType) { this.customerType = customerType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
