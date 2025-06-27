# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace/app
COPY . .
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests

# Runtime stage
FROM openjdk:21-jdk-slim
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /workspace/app/target/tracking-api-1.0.0.jar app.jar

# Expose the application port
EXPOSE 8089

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8089/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]