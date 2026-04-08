
FROM maven:3.9.9-eclipse-temurin-21 AS build
 
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
 
 
FROM eclipse-temurin:21-jdk-alpine
 
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
 
# Comando que passa TODAS as variáveis de ambiente para o Java
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
 