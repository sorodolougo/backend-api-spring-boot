/**
 * ====================================================================================
 * ROLE : CONTRAT DE SERVICE MÉTIER / BUSINESS LOGIC INTERFACE
 * RESPONSIBILITY : Définit les règles et les fonctionnalités applicatives du fichier clients.
 * Garantit le découpage strict (Découplage) entre l'API Web et l'accès à la base de données.
 * ARCHITECTURE : Patte de conception Clean Architecture pour isoler le cœur du domaine.
 * ====================================================================================
 */


package com.jeanfrancis.backendapispringboot.domain.customer.service;

import com.jeanfrancis.backendapispringboot.domain.customer.model.Customer;
import java.util.List;

public interface CustomerService {

    /**
     * Crée une nouvelle fiche client avec génération automatique de son code unique.
     * @param customer Les informations saisies dans le formulaire
     * @return Le client enregistré avec son ID et son code client généré
     */
    Customer createCustomer(Customer customer);

    /**
     * Récupère la liste complète des clients de l'ERP.
     */
    List<Customer> getAllCustomers();

    /**
     * Récupère un client par son identifiant unique.
     */
    Customer getCustomerById(Long id);

    /**
     * Met à jour les informations d'une fiche client existante.
     */
    Customer updateCustomer(Long id, Customer customerDetails);

    /**
     * Supprime définitivement un client de la base de données.
     */
    void deleteCustomer(Long id);
}
