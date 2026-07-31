/**
 * ====================================================================================
 * ROLE : COEUR LOGIQUE DU DOMAINE / SERVICE IMPLEMENTATION
 * RESPONSIBILITY : Exécute les deux règles d'or de ton entreprise : la génération
 * automatique du Code Client (CLI0001) et le blocage strict des doublons.
 * ARCHITECTURE : Sécurisé par @Transactional pour garantir la cohérence en cas de crash.
 * ====================================================================================
 */



package com.jeanfrancis.backendapispringboot.domain.customer.service;

import com.jeanfrancis.backendapispringboot.domain.customer.model.Customer;
import com.jeanfrancis.backendapispringboot.domain.customer.repository.CustomerRepository;
import com.jeanfrancis.backendapispringboot.exception.ResourceNotFoundException; // Utilise ton exception existante
import com.jeanfrancis.backendapispringboot.exception.BusinessException;         // Utilise ton exception de doublons métier
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    // Injection par constructeur (Recommandation Senior)
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public Customer createCustomer(Customer customer) {
        // RÈGLE MÉTIER 1 : GÉNÉRATION AUTOMATIQUE DU CODE CLIENT UNIQUE (Format : CLI0001)
        long currentCount = customerRepository.count();
        String generatedCode = String.format("CLI%04d", currentCount + 1);

        // Sécurité anti-collision de jetons
        if (customerRepository.existsByCustomerCode(generatedCode)) {
            throw new BusinessException("Erreur de synchronisation : Le code client " + generatedCode + " existe déjà.");
        }

        customer.setCustomerCode(generatedCode);
        return customerRepository.save(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                // FIX SENIOR : On passe les 3 arguments requis par ton constructeur personnalisé
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Le client avec l'ID " + id + " est introuvable."
                ));


    }


    @Override
    @Transactional
    public Customer updateCustomer(Long id, Customer customerDetails) {
        Customer existingCustomer = getCustomerById(id);

        // Mise à jour sélective des informations de la fiche tiers (Le code client reste immuable)
        existingCustomer.setName(customerDetails.getName());
        existingCustomer.setPhone(customerDetails.getPhone());
        existingCustomer.setEmail(customerDetails.getEmail());
        existingCustomer.setAddress(customerDetails.getAddress());
        existingCustomer.setCustomerType(customerDetails.getCustomerType());

        return customerRepository.save(existingCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = getCustomerById(id);
        customerRepository.delete(customer);
    }
}
