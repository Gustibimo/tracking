# Teleport Tracking Service

A high-performance, reactive tracking number generation service built with Spring Boot and R2DBC. This service generates unique tracking numbers for shipments with support for international shipping and customer-specific tracking.

## Features

- **Reactive API** - Built with Spring WebFlux for non-blocking I/O
- **Unique Tracking Numbers** - Ensures global uniqueness across all generated numbers
- **Multi-tenant Support** - Tracks numbers per customer with proper isolation
- **High Availability** - Designed for horizontal scaling
- **Monitoring** - Integrated with Micrometer and Prometheus
- **Database Migrations** - Version-controlled database schema using Flyway

## Tech Stack

- **Java 21** - Latest LTS version with modern language features
- **Spring Boot 3.2** - For building production-ready applications
- **R2DBC** - Reactive database access for non-blocking I/O
- **PostgreSQL** - Relational database for persistent storage
- **Redis** - For distributed locking and uniqueness guarantees
- **Flyway** - Database migration tool
- **Docker** - Containerization for easy deployment

## Architecture Decisions

### 1. Reactive Programming Model

**Why Spring WebFlux & R2DBC?**
- Handles high concurrency with a small number of threads
- Efficient resource utilization under heavy load
- Non-blocking I/O for database and network operations
- Better scalability for I/O-bound operations

### 2. Redis for Uniqueness Guarantees

**Why Redis?**
- **Distributed Locking**: Ensures only one instance processes a tracking number at a time
- **Temporary Storage**: Stores recently generated numbers to prevent race conditions
- **High Performance**: In-memory operations for fast lookups
- **TTL Support**: Automatically expires locks and temporary entries

### 3. Database Design

**PostgreSQL Features Used**:
- **SERIAL** for auto-incrementing IDs
- **TIMESTAMP WITH TIME ZONE** for consistent timezone handling
- **Indexes** on frequently queried fields
- **UNIQUE** constraint on tracking numbers

### 4. Idempotency & Retry Logic

- Exponential backoff for retries
- Idempotent operations to handle duplicate requests
- Transaction management for data consistency

## API Endpoints

### Generate Tracking Number

```http
GET /api/v1/next-tracking-number
```

**Query Parameters**:
- `originCountryId` (required): 2-letter country code (e.g., "ID")
- `destinationCountryId` (required): 2-letter country code (e.g., "SG")
- `weight` (required): Package weight in kg (0.001 - 999.999)
- `createdAt` (required): Order creation timestamp (ISO-8601 format)
- `customerId` (required): Customer UUID
- `customerName` (required): Customer name
- `customerSlug` (required): URL-friendly customer identifier

**Example Request**:
```bash
curl -X GET "http://localhost:8089/api/v1/next-tracking-number?originCountryId=ID&destinationCountryId=SG&weight=1.5&createdAt=2025-06-27T18:00:00%2B07:00&customerId=123e4567-e89b-12d3-a456-426614174000&customerName=Test%20Customer&customerSlug=test-customer"
```

production (railway):
```bash
curl -X GET "http://tracking-production-9118.up.railway.app/api/v1/next-tracking-number?originCountryId=ID&destinationCountryId=SG&weight=1.5&createdAt=2025-06-27T18:00:00%2B07:00&customerId=123e4567-e89b-12d3-a456-426614174000&customerName=Test%20Customer&customerSlug=test-customer"
````

## Getting Started

### Prerequisites

- Java 21
- PostgreSQL 13+
- Redis 6+
- Maven 3.8+

### Local Development

1. **Start Dependencies**
   ```bash
   docker-compose up -d
   ```

2. **Build the Application**
   ```bash
   ./mvnw clean package
   ```

3. **Run the Application**
   ```bash
   java -jar target/tracking-api-1.0.0.jar
   ```

## Database Migrations

Migrations are managed by Flyway and automatically applied on application startup. To create a new migration:

1. Create a new SQL file in `src/main/resources/db/migration/`
2. Follow the naming convention: `V{version}__{description}.sql`
3. The migration will be applied on the next application start

## Monitoring

The application exposes the following monitoring endpoints:

- `/actuator/health` - Application health status
- `/actuator/metrics` - Application metrics
- `/actuator/prometheus` - Prometheus metrics endpoint

## Performance Considerations

- **Caching**: Redis is used to cache frequently accessed data
- **Connection Pooling**: Optimized for high concurrency
- **Indexing**: Proper indexes on frequently queried columns
- **Batch Processing**: Supports bulk operations for high-volume scenarios

## Security

- Input validation on all endpoints
- Parameterized queries to prevent SQL injection
- Environment-specific configuration management
- Sensitive data is never logged

## Deployment

The application is containerized and can be deployed using Docker:

```bash
docker build -t teleport-service .
docker run -p 8089:8089 tracking-service
```

Deployed to Railway: https://tracking-production-9118.up.railway.app

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
