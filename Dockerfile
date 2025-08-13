# ===== Stage 1: Build the application =====
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Set work directory inside the container
WORKDIR /app

# Copy only pom.xml and download dependencies first (better cache)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application (skip tests for faster build)
RUN mvn clean package -DskipTests

# ===== Stage 2: Run the application =====
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copy the JAR from the build stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
