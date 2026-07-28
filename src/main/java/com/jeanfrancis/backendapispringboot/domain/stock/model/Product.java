// ====================================================================================
// ROLE : ENTITÉ COMPORTEMENTALE / MODÈLE DE DONNÉES JPA
// RESPONSIBILITY : Représente la cartographie exacte d'une ligne de la table "products"
// dans PostgreSQL. Elle utilise les annotations Jakarta Persistence (@Entity, @Column)
// pour configurer le type des colonnes, la taille, et l'unicité comptable (SKU).
// ARCHITECTURE : C'est l'objet pivot qui voyage à travers toutes les couches du serveur.
// ====================================================================================



package com.jeanfrancis.backendapispringboot.domain.stock.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "price_ht", nullable = false, precision = 15, scale = 4)
    private BigDecimal priceHt;

    // Sécurisation de la TVA : Augmenter la taille évite tout blocage en cas d'erreur de saisie
    @Column(name = "vat_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal vatRate;

    @Column(name = "current_stock", nullable = false)
    @Builder.Default
    private Integer currentStock = 0;
}
