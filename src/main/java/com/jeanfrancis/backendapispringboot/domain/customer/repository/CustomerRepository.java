/**
 * ====================================================================================
 * ROLE : PASSERELLE D'ACCÈS AUX DONNÉES / REPOSITORY LAYER
 * RESPONSIBILITY : Gère les requêtes SQL en tâche de fond sur ton PostgreSQL Cloud (Neon).
 * Fournit les fonctions natives de recherche (Sélection, Insertion, Suppression).
 * ARCHITECTURE : Hérite de JpaRepository pour automatiser la persistance sans écrire de SQL.
 * ====================================================================================
 */



package com.jeanfrancis.backendapispringboot.domain.customer.repository;

import com.jeanfrancis.backendapispringboot.domain.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Recherche un client par son code unique.
     * Utile pour empêcher les doublons lors des futures créations de fiches tiers.
     */
    Optional<Customer> findByCustomerCode(String customerCode);

    /**
     * Vérifie si un code client existe déjà dans l'ERP.
     */
    boolean existsByCustomerCode(String customerCode);
}
