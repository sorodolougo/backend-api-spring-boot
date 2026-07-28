// ====================================================================================
// ROLE : COUCHE D'ACCÈS AUX DONNÉES / DATA ACCESS OBJECT (DAO)
// RESPONSIBILITY : Hérite de JpaRepository pour automatiser les requêtes SQL vers PostgreSQL.
// Il fournit nativement les méthodes d'accès (save, findById, deleteById) et permet
// de déclarer des requêtes personnalisées (findBySku) via les mots-clés de Spring Data.
// ARCHITECTURE : Ne contient aucune logique métier, uniquement de l'infrastructure pure.
// ====================================================================================



package com.jeanfrancis.backendapispringboot.domain.stock.repository;

import com.jeanfrancis.backendapispringboot.domain.stock.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Cette méthode magique permettra plus tard de chercher un produit par son code-barres / SKU [1]
    Optional<Product> findBySku(String sku);
}
