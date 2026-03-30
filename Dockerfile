FROM eclipse-temurin:21-jdk-alpine
LABEL authors="victo"
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
