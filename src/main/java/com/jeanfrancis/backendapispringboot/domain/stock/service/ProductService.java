// ====================================================================================
// ROLE : INTERFACE MÉTIER (CONTRAT D'ARCHITECTURE DE LA COUCHE SERVICE)
// RESPONSIBILITY : Définit de manière abstraite et immuable toutes les actions CRUD
// que le module Stock doit être capable de réaliser pour l'application.
// ARCHITECTURE : Sert de passerelle isolante entre le Controller et l'implémentation.
// Elle garantit le découpage lâche et facilite les futurs tests unitaires (Mocks).
// ====================================================================================



// 1. Reflète l'emplacement géographique dans ton IDE
package com.jeanfrancis.backendapispringboot.domain.stock.service;

// 2. MISE À JOUR DE L'IMPORT : Pointe vers le nouveau dossier du modèle Product
import com.jeanfrancis.backendapispringboot.domain.stock.model.Product;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product createProduct(Product product);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
}
