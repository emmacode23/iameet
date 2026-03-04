# Étape 1 : Construction
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Étape 2 : Exécution
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Configuration Railway
ENV PORT=8080
EXPOSE 8080

# Limite mémoire à 300MB pour tenir sur le plan gratuit sans crasher
ENTRYPOINT ["java", "-Xmx300m", "-Xss512k", "-jar", "app.jar"]