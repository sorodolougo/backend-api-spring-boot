/**
 * ====================================================================================
 * ROLE : CONTRAT DE SERVICE MÉTIER TRANSACTIONNEL / INVOICE SERVICE CONTRACT
 * RESPONSIBILITY : Spécifie les fonctionnalités du registre des ventes.
 * Orchestre les flux de création de documents et de consolidation des stocks.
 * ARCHITECTURE : Patte de conception par Domaine étanche (DDD).
 * ====================================================================================
 */
package com.jeanfrancis.backendapispringboot.domain.invoice.service;

import com.jeanfrancis.backendapispringboot.domain.invoice.model.Invoice;
import com.jeanfrancis.backendapispringboot.domain.invoice.model.InvoiceLine; // 🟢 FIX SENIOR : Importation propre de la classe de domaine
import java.util.List;

public interface InvoiceService {

    /**
     * Émet une facture commerciale avec numérotation séquentielle et déstockage en direct.
     * @param customerId L'identifiant technique du client
     * @param linesDraft Le panier d'achat brut envoyé par Angular (contenant uniquement le product_id et quantity)
     * @return La facture matérialisée avec ses calculs fiscaux finaux
     */
    // 🟢 OPTIMISATION VISUELLE : Signature de méthode épurée et lisible
    Invoice createInvoice(Long customerId, List<InvoiceLine> linesDraft);

    /**
     * Récupère le grand livre comptable de toutes les factures.
     */
    List<Invoice> getAllInvoices();

    /**
     * Récupère une facture par son ID pour inspection.
     */
    Invoice getInvoiceById(Long id);

    List<Invoice> getInvoicesByCustomerId(Long customerId);

}
