// ====================================================================================
// ROLE : IMPLÉMENTATION CONCRÈTE DU SERVICE (LE CERVEAU MÉTIER DU DOMAINE STOCK)
// RESPONSIBILITY : Exécute la vraie logique de l'ERP. C'est ici qu'on ouvre les
// transactions SQL (@Transactional), qu'on applique les règles d'intégrité (vérification
// des doublons de SKU), et qu'on lève les exceptions métiers (EntityNotFoundException).
// ARCHITECTURE : Injecte le Repository par constructeur pour persister dans PostgreSQL.
// ====================================================================================


package com.jeanfrancis.backendapispringboot.domain.stock.service;

import com.jeanfrancis.backendapispringboot.domain.stock.model.Product;
import com.jeanfrancis.backendapispringboot.domain.stock.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException; // <-- NOUVEL IMPORT STRUCTUREL
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    // Injection par constructeur (Règle d'or Senior)
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true) // Optimise les performances de lecture JPA
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                // FIX SENIOR : On lance EntityNotFoundException pour forcer un code HTTP 404 propre en JSON
                .orElseThrow(() -> new EntityNotFoundException("Le produit avec l'ID '" + id + "' n'existe pas dans le catalogue."));
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        if (productRepository.findBySku(product.getSku()).isPresent()) {
            // Déclenche l'interception 1 (HTTP 400 Bad Request) de notre gestionnaire global
            throw new IllegalArgumentException("Le code SKU '" + product.getSku() + "' est déjà attribué à un autre article.");
        }
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = this.getProductById(id);

        // Validation d'unicité du SKU en cas de modification frauduleuse
        if (!existingProduct.getSku().equals(updatedProduct.getSku())) {
            if (productRepository.findBySku(updatedProduct.getSku()).isPresent()) {
                throw new IllegalArgumentException("Le SKU '" + updatedProduct.getSku() + "' est déjà utilisé par un autre produit.");
            }
            existingProduct.setSku(updatedProduct.getSku());
        }

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setPriceHt(updatedProduct.getPriceHt());
        existingProduct.setVatRate(updatedProduct.getVatRate());
        existingProduct.setCurrentStock(updatedProduct.getCurrentStock());

        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            // FIX SENIOR : Garantit un retour 404 si l'utilisateur tente de supprimer un ID fantôme
            throw new EntityNotFoundException("Impossible de supprimer : le produit avec l'ID '" + id + "' est introuvable.");
        }
        productRepository.deleteById(id);
    }
}
