/**
 * ====================================================================================
 * ROLE : POINT D'ENTRÉE DE L'API / REST CONTROLLER LAYER
 * RESPONSIBILITY : Ouvre les vannes du serveur pour écouter ton application Angular.
 * Intercepte les requêtes HTTP (GET, POST, PUT, DELETE) et les transforme en JSON propre.
 * ARCHITECTURE : Centralise le routage sur /api/v1/customers et alimente le Swagger Cloud.
 * ====================================================================================
 */



package com.jeanfrancis.backendapispringboot.domain.customer.controller;

import com.jeanfrancis.backendapispringboot.domain.customer.model.Customer;
import com.jeanfrancis.backendapispringboot.domain.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    // Injection par constructeur (Recommandation Senior)
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * POST /api/v1/customers
     * Crée un nouveau client avec génération de code automatique.
     */
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
        Customer createdCustomer = customerService.createCustomer(customer);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    /**
     * GET /api/v1/customers
     * Récupère la liste complète de tous les clients de la GesCom.
     */
    @GetMapping // <-- Correction ici : G majuscule obligatoire !
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }


    /**
     * GET /api/v1/customers/{id}
     * Récupère les détails complets d'une fiche client par son ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    /**
     * PUT /api/v1/customers/{id}
     * Met à jour de manière sélective une fiche client existante.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @Valid @RequestBody Customer customerDetails) {
        Customer updatedCustomer = customerService.updateCustomer(id, customerDetails);
        return ResponseEntity.ok(updatedCustomer);
    }

    /**
     * DELETE /api/v1/customers/{id}
     * Supprime définitivement un client de PostgreSQL.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
