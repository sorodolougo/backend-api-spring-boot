/**
 * ====================================================================================
 * ROLE : POINT D'ENTRÉE DE L'API TRANSACTIONNELLE / INVOICE REST CONTROLLER
 * RESPONSIBILITY : Ouvre les vannes du serveur pour intercepter les paniers d'achats.
 * Réceptionne le DTO d'émission, appelle le déstockage et expose le grand livre des ventes.
 * ARCHITECTURE : Centralise le routage sur /api/v1/invoices et s'aligne sur le Swagger.
 * ====================================================================================
 */
package com.jeanfrancis.backendapispringboot.domain.invoice.controller;

import com.jeanfrancis.backendapispringboot.domain.invoice.dto.InvoiceRequestDto;
import com.jeanfrancis.backendapispringboot.domain.invoice.model.Invoice;
import com.jeanfrancis.backendapispringboot.domain.invoice.model.InvoiceLine;
import com.jeanfrancis.backendapispringboot.domain.invoice.service.InvoiceService;
import com.jeanfrancis.backendapispringboot.domain.stock.model.Product; // Aligné sur ton package stock
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    // Injection Senior Pro par constructeur
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    /**
     * POST /api/v1/invoices
     * Émet une facture commerciale avec déstockage atomique en temps réel.
     */
    /**
     * POST /api/v1/invoices
     * Émet une facture commerciale avec déstockage atomique en temps réel.
     */
    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@Valid @RequestBody InvoiceRequestDto requestDto) {

        // MAPPING SÉCURISÉ : On instancie proprement la structure pour éviter le NullPointerException
        List<InvoiceLine> domainLines = requestDto.getLines().stream().map(dto -> {
            InvoiceLine line = new InvoiceLine();

            // FIX SENIOR : Initialisation explicite du produit lié à la ligne
            Product placeholderProduct = new Product();
            placeholderProduct.setId(dto.getProductId()); // Injection de l'ID pour la recherche du service

            line.setProduct(placeholderProduct);
            line.setQuantity(dto.getQuantity());
            return line;
        }).collect(Collectors.toList());

        // Déclenchement du moteur transactionnel sécurisé
        Invoice createdInvoice = invoiceService.createInvoice(requestDto.getCustomerId(), domainLines);
        return new ResponseEntity<>(createdInvoice, HttpStatus.CREATED);
    }

    /**
     * GET /api/v1/invoices
     * Récupère le grand livre comptable de toutes les factures émises.
     */
    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        List<Invoice> invoices = invoiceService.getAllInvoices();
        return ResponseEntity.ok(invoices);
    }

    /**
     * GET /api/v1/invoices/{id}
     * Récupère les spécifications complètes d'une facture par son ID technique.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id) {
        Invoice invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(invoice);
    }
}
