# Étape 1 : Build
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# On copie le gradle wrapper et les fichiers de config
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# On copie les modules nécessaires (proto et ingestor)
COPY proto-schema proto-schema
COPY log-ingestor log-ingestor
COPY log-sdk-starter log-sdk-starter

# Compilation (on skip les tests pour gagner du temps ici)
RUN ./gradlew :log-ingestor:bootJar -x test

# Étape 2 : Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# On crée un utilisateur non-root pour la sécurité
RUN useradd -m myuser
USER myuser

# On récupère uniquement le JAR compilé
COPY --from=build /app/log-ingestor/build/libs/*.jar app.jar

# Ports gRPC (9090) et Actuator/Health (8080)
EXPOSE 9090 8080

# Utilisation des Virtual Threads optimisée au démarrage
ENTRYPOINT ["java", "-Dspring.threads.virtual.enabled=true", "-jar", "app.jar"]