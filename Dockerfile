# Stage 1 - Build
FROM maven:3.9-eclipse-temurin-22 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2 - Run
FROM eclipse-temurin:22-jre
WORKDIR /app
EXPOSE 9090
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
