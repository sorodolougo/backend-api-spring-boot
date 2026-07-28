package com.jeanfrancis.backendapispringboot.domain.stock.service;

import com.jeanfrancis.backendapispringboot.domain.stock.model.Product;
import com.jeanfrancis.backendapispringboot.domain.stock.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Active l'outillage Mockito pour JUnit 5
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository; // Simule la couche d'infrastructure BDD

    @InjectMocks
    private ProductServiceImpl productService; // Injecte automatiquement le mock ci-dessus dans ton service

    @Test
    @DisplayName("SCÉNARIO NOMINAL : Création d'un produit avec succès")
    void createProduct_ShouldSaveAndReturnProduct_WhenSkuIsUnique() {
        // 1. GIVEN (Préparation des données de test et des comportements attendus)
        Product inputProduct = new Product();
        inputProduct.setSku("PRD-TEST");
        inputProduct.setName("Ordinateur Portable");
        inputProduct.setPriceHt(new BigDecimal("500000"));
        inputProduct.setVatRate(new BigDecimal("18.00"));
        inputProduct.setCurrentStock(10);

        Product savedProduct = new Product();
        savedProduct.setId(1L); // Simule l'ID auto-généré par PostgreSQL
        savedProduct.setSku("PRD-TEST");
        savedProduct.setName("Ordinateur Portable");

        // On ordonne au simulacre de répondre "vide" quand on cherche le SKU (Preuve qu'il est unique)
        when(productRepository.findBySku("PRD-TEST")).thenReturn(Optional.empty());
        // On ordonne au simulacre de renvoyer l'objet final lors de l'appel au .save()
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // 2. WHEN (Exécution de l'action métier réelle de ton service)
        Product result = productService.createProduct(inputProduct);

        // 3. THEN (Vérifications formelles des résultats et des appels)
        assertNotNull(result, "Le produit retourné ne doit pas être null");
        assertEquals(1L, result.getId(), "L'ID retourné doit correspondre à la simulation");
        assertEquals("PRD-TEST", result.getSku(), "Le SKU doit rester inchangé");

        // Sécurité Senior : On vérifie que la BDD a bien été sollicitée exactement 1 fois pour l'écriture
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("SCÉNARIO AUX LIMITES : Échec de création si le code SKU existe déjà")
    void createProduct_ShouldThrowIllegalArgumentException_WhenSkuAlreadyExists() {
        // 1. GIVEN
        Product existingProduct = new Product();
        existingProduct.setSku("PRD-DOUBLON");

        Product newProductWithSameSku = new Product();
        newProductWithSameSku.setSku("PRD-DOUBLON");

        // On simule le fait que la base de données trouve déjà un produit avec ce SKU
        when(productRepository.findBySku("PRD-DOUBLON")).thenReturn(Optional.of(existingProduct));

        // 2. WHEN & 3. THEN (Vérifie que la bonne exception est levée avec le bon message français)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(newProductWithSameSku);
        });

        assertEquals("Le code SKU 'PRD-DOUBLON' est déjà attribué à un autre article.", exception.getMessage());

        // Sécurité Senior : On s'assure qu'Hibernate n'a JAMAIS tenté d'exécuter un insert SQL
        verify(productRepository, never()).save(any(Product.class));
    }
}
