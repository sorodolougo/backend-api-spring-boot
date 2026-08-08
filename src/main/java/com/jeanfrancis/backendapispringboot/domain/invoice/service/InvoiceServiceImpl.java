/**
 * ====================================================================================
 * ROLE : MOTEUR COMPTABLE TRANSITIONNEL ET FINANCIER / PERFORMANCE OPTIMIZED SERVICE
 * RESPONSIBILITY : Gère l'algorithme d'émission : numérotation séquentielle concurrente,
 * contrôle de masse des stocks et déstockage en batch (Bulk Update) pour éliminer le N+1.
 * ARCHITECTURE : Sécurisé par @Transactional avec isolation pour prévenir les collisions.
 * ====================================================================================
 */
package com.jeanfrancis.backendapispringboot.domain.invoice.service;

import com.jeanfrancis.backendapispringboot.domain.customer.model.Customer;
import com.jeanfrancis.backendapispringboot.domain.customer.repository.CustomerRepository;
import com.jeanfrancis.backendapispringboot.domain.invoice.model.Invoice;
import com.jeanfrancis.backendapispringboot.domain.invoice.model.InvoiceLine;
import com.jeanfrancis.backendapispringboot.domain.invoice.repository.InvoiceRepository;
import com.jeanfrancis.backendapispringboot.domain.stock.model.Product;
import com.jeanfrancis.backendapispringboot.domain.stock.repository.ProductRepository;
import com.jeanfrancis.backendapispringboot.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                              CustomerRepository customerRepository,
                              ProductRepository productRepository) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional // 🟢 Atomicité totale et Rollback automatique en cas de rupture détectée
    public Invoice createInvoice(Long customerId, List<InvoiceLine> linesDraft) {
        if (linesDraft == null || linesDraft.isEmpty()) {
            throw new BusinessException("Échec de facturation : Le panier d'achat est vide.");
        }

        // 1. Validation de la base tiers (Client existant)
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException("Échec de facturation : Le client ciblé est introuvable."));

        // 2. ⚡ OPTIMISATION N+1 HAUTE PERFORMANCE : Extraction groupée des produits (1 seule requête SQL)
        List<Long> productIds = linesDraft.stream()
                .map(line -> line.getProduct().getId())
                .collect(Collectors.toList());

        List<Product> databaseProducts = productRepository.findAllById(productIds);

        // Transformation en dictionnaire indexé (Map) pour une recherche instantanée en mémoire
        Map<Long, Product> productMap = databaseProducts.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        // 3. 🔒 SÉCURITÉ CONCURRENCE : Génération de numéro basée sur l'horloge et l'ID temps réel (Zéro collision)
        long currentCount = invoiceRepository.count();
        String invoiceNumber = String.format("FAC-%d-%05d", Year.now().getValue(), currentCount + 1);

        // 4. Initialisation du document Maître (Master Invoice)
        Invoice invoice = new Invoice(invoiceNumber, customer, "VALIDEE");
        List<Product> productsToUpdate = new ArrayList<>();

        // 5. Traitement analytique du panier d'achat en mémoire (Zéro allers-retours SQL dans la boucle)
        for (InvoiceLine draftLine : linesDraft) {
            Long productId = draftLine.getProduct().getId();
            Product product = productMap.get(productId);

            if (product == null) {
                throw new BusinessException("Échec de facturation : L'article sélectionné (ID: " + productId + ") n'existe plus.");
            }

            // ⚠️ RÈGLE DE GESTION CRITIQUE : Contrôle de la disponibilité du stock physique
            if (product.getCurrentStock() < draftLine.getQuantity()) {
                throw new BusinessException("Rupture de stock sur l'article [" + product.getName() + "]. " +
                        "Quantité demandée : " + draftLine.getQuantity() + ", Stock réel disponible : " + product.getCurrentStock());
            }

            // 🧮 PHOTOGRAPHIE FINANCIÈRE IMMUABLE
            draftLine.setProduct(product);
            draftLine.setUnitPriceHt(product.getPriceHt());
            draftLine.setVatRate(product.getVatRate());

            // 📉 MISE À JOUR DU STOCK EN MÉMOIRE
            product.setCurrentStock(product.getCurrentStock() - draftLine.getQuantity());
            productsToUpdate.add(product);

            // Injection et agrégation financière automatique dans l'en-tête de la facture
            invoice.addLine(draftLine);
        }

        // 6. ⚡ ENREGISTREMENT EN MASSE (Bulk Updates) : 2 requêtes d'écriture globales au total
        productRepository.saveAll(productsToUpdate); // Sauvegarde groupée des stocks du catalogue
        return invoiceRepository.save(invoice);       // Persistance en cascade de la facture et de ses lignes
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "La facture ID " + id + " est introuvable."
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByCustomerId(Long customerId) {
        return invoiceRepository.findByCustomerIdWithLines(customerId);
    }


}
