# Étape 1 : Construction avec Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Étape 2 : Exécution avec Java 21 (Image standard pour stabilité)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Configuration des variables d'environnement
ENV PORT=8080
EXPOSE 8080

# Commande de lancement avec optimisation mémoire pour petit serveur
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]