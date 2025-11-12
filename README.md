# TODO Management Backend

Backend API service for the TODO Management Application built with Java 17 and Spring Boot 3.x.

## Prerequisites

- Java 17 or later
- Maven 3.9.x or later
- PostgreSQL 15.x (for production) or H2 (for testing)

## Getting Started

### Build the Project

```bash
mvn clean install
```

### Run Locally (Development Mode)

1. Start PostgreSQL database:
```bash
docker run --name postgres-todo \
  -e POSTGRES_DB=todoapp \
  -e POSTGRES_USER=todoapp \
  -e POSTGRES_PASSWORD=todoapp \
  -p 5432:5432 \
  -d postgres:15-alpine
```

2. Run the application:
```bash
mvn spring-boot:run
```

Or with dev profile (uses H2):
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The API will be available at `http://localhost:8080`

### Run Tests

```bash
mvn test
```

## API Endpoints

### Create Task
**POST** `/api/v1/tasks`

Request body:
```json
{
  "description": "Buy groceries"
}
```

Response (201 Created):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "description": "Buy groceries",
  "completed": false,
  "status": "ACTIVE",
  "createdAt": "2025-11-12T10:00:00Z",
  "updatedAt": "2025-11-12T10:00:00Z"
}
```

## Project Structure

```
todo-management-backend/
├── src/
│   ├── main/
│   │   ├── java/com/todoapp/
│   │   │   ├── api/dto/           # API contracts and DTOs
│   │   │   ├── domain/            # Domain models
│   │   │   ├── repository/        # Data access layer
│   │   │   ├── service/           # Business logic
│   │   │   ├── controller/        # REST controllers
│   │   │   ├── exception/         # Exception handlers
│   │   │   └── TodoApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── db/migration/      # Flyway migrations
│   └── test/
│       └── java/com/todoapp/
└── pom.xml
```

## Technology Stack

- **Java 17** - Programming language
- **Spring Boot 3.2.0** - Application framework
- **Spring Data JPA** - Data access layer
- **PostgreSQL** - Production database
- **H2** - Test database
- **Flyway** - Database migrations
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework

## Configuration

Database configuration can be adjusted in `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/todoapp
    username: todoapp
    password: todoapp
```

## Implemented Features

- ✅ US-1.1: Create TODO Item
  - POST endpoint for creating tasks
  - Input validation (non-empty, max 500 chars)
  - Error handling with meaningful messages

## Future Features

- US-1.2: View TODO List
- US-1.3: Mark Task as Complete
- US-1.4: Delete TODO Item
- US-1.5: Persist Tasks (additional features)

## License

This project is part of a workshop and is for educational purposes.
