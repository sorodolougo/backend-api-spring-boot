# ====================================================================================
# ÉTAPE 1 : COMPILATION DE L'ERP (BUILD ENVIRONMENT)
# ====================================================================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copie de la plomberie Maven pour mettre en cache les dépendances
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copie du code source et compilation de l'application en ignorant les tests (déjà validés par la CI)
COPY src ./src
RUN mvn package -DskipTests -B

# ====================================================================================
# ÉTAPE 2 : EXÉCUTION EN PRODUCTION (RUNTIME ENVIRONMENT)
# ====================================================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copie du fichier .jar généré à l'étape précédente
COPY --from=build /app/target/*.jar app.jar

# Port d'écoute standard pour Render
EXPOSE 8080

# Commande de démarrage optimale pour Spring Boot en production
ENTRYPOINT ["java", "-jar", "app.jar"]
