FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/tracking-api-1.0.0.jar app.jar

EXPOSE 8089

HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8089/api/v1/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]