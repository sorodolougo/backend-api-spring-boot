/**
 * ====================================================================================
 * ROLE : ROBOT D'INITIALISATION DU FICHIER TIERS / DATA SEEDER
 * RESPONSIBILITY : S'exécute automatiquement à l'allumage du serveur. Si la table
 * "customers" est vide, il peuple le Cloud Neon avec un jeu de PME réalistes.
 * ARCHITECTURE : Implémente CommandLineRunner, injecté proprement par constructeur.
 * ====================================================================================
 */
package com.jeanfrancis.backendapispringboot.domain.customer.config; // Ajuste le package selon ton arborescence

import com.jeanfrancis.backendapispringboot.domain.customer.model.Customer;
import com.jeanfrancis.backendapispringboot.domain.customer.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CustomerDataInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;

    // Injection Senior Pro par constructeur
    public CustomerDataInitializer(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Condition de sécurité : On n'injecte QUE si la base Cloud est totalement vierge
        if (customerRepository.count() == 0) {

            List<Customer> demoCustomers = List.of(
                    new Customer("CLI0001", "Quincaillerie Moderne du Centre", "0708091011", "contact@quincaillerie-centre.ci", "Bouaké, Quartier Commerce", "ENTREPRISE"),
                    new Customer("CLI0002", "Dépôt de Boissons Soro & Frères", "0102030405", "soro.depot@gmail.com", "Dabou, Face Gare Routière", "ENTREPRISE"),
                    new Customer("CLI0003", "Supermarché Étoile d'Afrique", "2722405060", "direction@etoile-afrique.com", "Abidjan, Cocody Angré", "ENTREPRISE"),
                    new Customer("CLI0004", "Boutique Mode & Tendances", "0506070809", "mariam.mode@yahoo.fr", "Yamoussoukro, Non loin de la Basilique", "PARTICULIER"),
                    new Customer("CLI0005", "Koffi Yao Jean-Francis", "0749123456", "jeanfrancis.dev@outlook.com", "Abidjan, Yopougon Maroc", "PARTICULIER")
            );

            // Persistance de masse (Bulk Insert) sur ton PostgreSQL Cloud Neon
            customerRepository.saveAll(demoCustomers);

            System.out.println("====================================================================================");
            System.out.println("[GESCOM ERP INFO] : Fichier clients vide déctecté. Injection de 5 PME de test réussie !");
            System.out.println("====================================================================================");
        } else {
            System.out.println("[GESCOM ERP INFO] : Le fichier clients contient déjà des enregistrements. Skip de l'initialisation.");
        }
    }
}
