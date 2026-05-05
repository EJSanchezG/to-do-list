# Etapa 1: Construcción (Maven)
FROM maven:3.9.15-eclipse-temurin-25 AS build
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (JRE)
FROM eclipse-temurin:25-jre
COPY --from=build /target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]