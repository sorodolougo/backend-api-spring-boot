// 1. LA LIGNE DE PACKAGE DOIT TOUJOURS ÊTRE EN POSITION NUMÉRO 1
package com.jeanfrancis.backendapispringboot;


// ====================================================================================
// ROLE : TEST D'INTÉGRATION RACINE (SMOKE TEST / TEST DE FUMÉE)
// RESPONSIBILITY : S'assure que le contexte Spring Boot démarre correctement et que
// toute la plomberie d'infrastructure (Hibernate 7, Spring Boot 4) est saine.
// FIX PRODUCTION : On injecte des valeurs de secours pour empêcher le crash de dialecte.
// ====================================================================================

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// FIX PRODUCTION SENIOR : Utilisation d'une base H2 ultra-rapide en mémoire
// pour décorréler le build Maven de PostgreSQL local ou cloud.
@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:gescom_test_db;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"spring.jpa.hibernate.ddl-auto=create-drop"
})
class BackendApiSpringBootApplicationTests {

	@Test
	void contextLoads() {
		// Valide le chargement complet de l'écosystème Spring sans aucune dépendance physique extérieure.
	}
}
