FROM eclipse-temurin:17-jdk-alpine
LABEL authors="victo"
WORKDIR /app
COPY target/estoque.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
