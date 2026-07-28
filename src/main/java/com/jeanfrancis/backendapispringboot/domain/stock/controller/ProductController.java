// ====================================================================================
// ROLE : CONTROLLER REST (POINT D'ENTRÉE API HTTP)
// RESPONSIBILITY : Reçoit les requêtes HTTP d'Angular, extrait les variables d'URL
// (ex: @PathVariable id) ou le JSON du body (@RequestBody), puis délègue le traitement
// à la couche Service. Il retourne uniquement des codes HTTP standards (200, 204).
// ARCHITECTURE : Ne contient aucune logique métier et ne parle JAMAIS à la base de données.
// DOCUMENTATION : Embarque les annotations OpenAPI/Swagger pour générer l'IHM de test.
// ====================================================================================
package com.jeanfrancis.backendapispringboot.domain.stock.controller;

import com.jeanfrancis.backendapispringboot.domain.stock.model.Product;
import com.jeanfrancis.backendapispringboot.domain.stock.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Catalogue Produits", description = "Endpoints CRUD de gestion de l'inventaire et des articles de la GesCom")
public class ProductController {

    private final ProductService productService;

    // Le contrôleur ne connaît plus la base de données, il parle uniquement à la couche service
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Lister tous les produits", description = "Récupère l'intégralité du catalogue des articles pour l'affichage du grand tableau ou des grilles de cartes.")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Rechercher un produit par son ID", description = "Retourne une fiche produit unique pour l'injection .patchValue() du formulaire. Renvoie une erreur 404 si l'identifiant n'existe pas.")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau produit", description = "Insère une nouvelle référence article en base de données. Bloque l'action (HTTP 400) si le SKU existe déjà.")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productService.createProduct(product));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une fiche produit complète", description = "Met à jour l'ensemble des données d'un article par son identifiant unique (Désignation, Prix HT, TVA, Stock).")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un produit du catalogue", description = "Retire définitivement l'article de la base de données PostgreSQL. Renvoie un code standard REST 204 No Content.")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
