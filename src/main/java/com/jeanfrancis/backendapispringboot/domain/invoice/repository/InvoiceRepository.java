/**
 * ====================================================================================
 * ROLE : PASSERELLE D'ACCÈS AUX DONNÉES / REPOSITORY LAYER (PERFORMANCE OPTIMIZED)
 * RESPONSIBILITY : Pilote les requêtes SQL complexes sur la table "invoices".
 * Intègre des requêtes par jointure (FETCH JOIN) pour éradiquer le problème N+1.
 * ARCHITECTURE : Hérite de JpaRepository pour automatiser la persistance Serverless Neon.
 * ====================================================================================
 */
package com.jeanfrancis.backendapispringboot.domain.invoice.repository;

import com.jeanfrancis.backendapispringboot.domain.invoice.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    /**
     * Recherche une facture par son numéro de pièce unique commercial.
     */
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    /**
     * Vérifie si un numéro de facture existe déjà pour bloquer les collisions.
     */
    boolean existsByInvoiceNumber(String invoiceNumber);

    /**
     * ⚡ OPTIMISATION PERFORMANCE : Récupère toutes les factures avec leurs clients
     * et leurs lignes associées en UNE SEULE requête SQL globale.
     */
    @Query("SELECT DISTINCT i FROM Invoice i " +
            "LEFT JOIN FETCH i.customer " +
            "LEFT JOIN FETCH i.invoiceLines")
    List<Invoice> findAllWithLinesAndCustomer();

    /**
     * ⚡ OPTIMISATION PERFORMANCE : Récupère une facture spécifique avec l'intégralité
     * de son panier d'achat et son client en un seul aller-retour SQL.
     */
    @Query("SELECT i FROM Invoice i " +
            "LEFT JOIN FETCH i.customer " +
            "LEFT JOIN FETCH i.invoiceLines " +
            "WHERE i.id = :id")
    Optional<Invoice> findByIdWithLinesAndCustomer(@Param("id") Long id);

    /**
     * ⚡ OPTIMISATION PERFORMANCE : Extrait l'historique d'achat complet d'un client unique
     * avec le détail de ses lignes en une seule requête SQL par jointure.
     * @param customerId L'identifiant technique du tiers client
     */
    @Query("SELECT DISTINCT i FROM Invoice i " +
            "LEFT JOIN FETCH i.invoiceLines " +
            "WHERE i.customer.id = :customerId " +
            "ORDER BY i.createdAt DESC") // Les factures les plus récentes s'affichent en premier
    List<Invoice> findByCustomerIdWithLines(@Param("customerId") Long customerId);


}
