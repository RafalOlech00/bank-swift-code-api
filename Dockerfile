# Używamy obrazu Javy 17 (można zmienić na 21 jeśli potrzebujesz)
FROM eclipse-temurin:17-jdk-alpine

# Ustawiamy katalog roboczy
WORKDIR /app

# Kopiujemy plik JAR do kontenera
COPY target/swiftapi-0.0.1-SNAPSHOT.jar app.jar

# Port aplikacji
EXPOSE 8080

# Komenda do uruchomienia Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
