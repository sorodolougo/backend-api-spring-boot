package com.jeanfrancis.backendapispringboot;

import com.jeanfrancis.backendapispringboot.domain.stock.model.Product;
import com.jeanfrancis.backendapispringboot.domain.stock.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            System.out.println("🚀 Base de données vide. Injection des produits de test...");

            Product p1 = new Product();
            p1.setSku("PROD-PC-01");
            p1.setName("Ordinateur Portable Pro");
            p1.setPriceHt(new BigDecimal("1250.0000"));
            p1.setVatRate(new BigDecimal("20.00"));
            p1.setCurrentStock(15);
            productRepository.save(p1);

            Product p2 = new Product();
            p2.setSku("PROD-MS-02");
            p2.setName("Souris Ergonomique Sans Fil");
            p2.setPriceHt(new BigDecimal("45.5000"));
            p2.setVatRate(new BigDecimal("20.00"));
            p2.setCurrentStock(120);
            productRepository.save(p2);

            System.out.println("✅ Données de test injectées avec succès !");
        }
    }
}
