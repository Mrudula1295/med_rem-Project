# Stage 1: Build the Maven application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Compile and package without running tests to speed up the build
RUN mvn clean package -DskipTests

# Stage 2: Create a minimal image containing only the executable JAR
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Explicitly expose port 8080 (Mapped by Render dynamically)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
