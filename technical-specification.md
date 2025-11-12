# Technical Specification: TODO Management Application - Backend API

**Document Version:** 1.0  
**Date:** 2025-11-12  
**Status:** Draft for Review  
**Project:** Simple TODO Management Application - Backend Service  
**Technology Stack:** Java 17, Spring Boot 3.x, PostgreSQL

---

## 1. Executive Summary

### 1.1 Purpose

This technical specification defines the architecture, design, and implementation details for a RESTful backend API service that powers a TODO Management Application. The service provides core task management capabilities including CRUD operations, prioritization, categorization, filtering, and search functionality.

### 1.2 Scope

**In Scope:**
- RESTful API for TODO task management
- Data persistence with PostgreSQL database
- Authentication and authorization
- Task CRUD operations, completion tracking, soft delete
- Task prioritization, categorization, filtering, and search
- Due date management and overdue detection
- Data export/import capabilities
- API documentation (OpenAPI 3.0)
- Health checks and observability

**Out of Scope (Future Phases):**
- Real-time collaboration features
- File attachments
- Email notifications
- Mobile push notifications
- Third-party integrations (calendar, etc.)

### 1.3 Stakeholders

- **Product Owner:** Defines requirements and priorities
- **Backend Development Team:** Implements the service
- **Frontend Development Team:** Consumes the API
- **QA Team:** Tests functionality and performance
- **DevOps/SRE:** Deploys and operates the service
- **End Users:** Individuals managing personal tasks

### 1.4 Success Criteria

- API response time < 200ms (p95) for standard operations
- Uptime > 99.5%
- Test coverage > 80%
- Zero critical security vulnerabilities
- API documentation completeness score > 95%
- Successful load test: 100 concurrent users, 1000 RPS

---

## 2. Architecture Overview

### 2.1 High-Level Architecture

```
┌─────────────────┐
│  Web Frontend   │
│   (React/Vue)   │
└────────┬────────┘
         │ HTTPS
         │
┌────────▼────────────────────────────────────┐
│           API Gateway / Load Balancer       │
│              (nginx/ALB)                    │
└────────┬────────────────────────────────────┘
         │
┌────────▼────────────────────────────────────┐
│        TODO Management Backend API          │
│                                             │
│  ┌─────────────────────────────────────┐  │
│  │     Controller Layer (REST)         │  │
│  └──────────────┬──────────────────────┘  │
│                 │                          │
│  ┌──────────────▼──────────────────────┐  │
│  │      Service Layer (Business)       │  │
│  └──────────────┬──────────────────────┘  │
│                 │                          │
│  ┌──────────────▼──────────────────────┐  │
│  │    Repository Layer (Data Access)   │  │
│  └──────────────┬──────────────────────┘  │
└─────────────────┼──────────────────────────┘
                  │
         ┌────────▼────────┐
         │   PostgreSQL    │
         │    Database     │
         └─────────────────┘
```

### 2.2 Architecture Style

**Layered Architecture (Clean Architecture Principles)**

- **Presentation Layer (Controllers):** Handles HTTP requests/responses, input validation, DTO mapping
- **Business Layer (Services):** Contains domain logic, orchestration, transaction management
- **Data Access Layer (Repositories):** Database operations, query execution
- **Domain Layer (Models/Entities):** Core domain objects, business rules

**Key Principles:**
- Dependency inversion: Inner layers don't depend on outer layers
- Single Responsibility: Each layer has a clear purpose
- Interface segregation: Define clear contracts between layers
- Separation of Concerns: Keep business logic independent of frameworks

### 2.3 Architecture Patterns

| Pattern | Usage | Rationale |
|---------|-------|-----------|
| **MVC (Model-View-Controller)** | REST controllers, services, entities | Spring Boot standard, clear separation |
| **Repository Pattern** | Data access abstraction | Encapsulates persistence logic, testability |
| **DTO Pattern** | Request/Response objects | Decouples API contracts from domain models |
| **Service Layer Pattern** | Business logic orchestration | Centralizes transaction management |
| **Builder Pattern** | Complex object creation | Improves readability for entities with many fields |
| **Strategy Pattern** | Search/filter implementations | Extensible filtering and sorting logic |

---

## 3. Technology Stack & Justification

### 3.1 Core Technologies

| Component | Technology | Version | Justification |
|-----------|-----------|---------|---------------|
| **Language** | Java | 17 LTS | Long-term support, modern features (records, sealed classes), wide ecosystem |
| **Framework** | Spring Boot | 3.2.x | Industry standard, comprehensive ecosystem, production-ready features |
| **Build Tool** | Maven | 3.9.x | Widely adopted, extensive plugin ecosystem, reliable dependency management |
| **Database** | PostgreSQL | 15.x | ACID compliance, JSON support, excellent performance, open-source |
| **ORM** | Spring Data JPA (Hibernate) | 3.2.x | Reduces boilerplate, query methods, native SQL support when needed |
| **API Documentation** | SpringDoc OpenAPI | 2.3.x | Auto-generates OpenAPI 3.0 spec, Swagger UI integration |
| **Security** | Spring Security | 6.2.x | OAuth2/JWT support, comprehensive security features |
| **Testing** | JUnit 5 + Mockito | Latest | Standard testing framework, extensive mock support |
| **Validation** | Jakarta Validation | 3.0.x | Declarative validation, reduces boilerplate |
| **Logging** | SLF4J + Logback | Latest | Flexible, structured logging, JSON output support |
| **Observability** | Micrometer + Actuator | Latest | Metrics, health checks, production-ready features |

### 3.2 Additional Dependencies

```xml
<!-- Key dependencies -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

---

## 4. Data Model

### 4.1 Domain Model

#### Task Entity

```java
@Entity
@Table(name = "tasks", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_due_date", columnList = "due_date")
})
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, length = 500)
    @Size(min = 1, max = 500)
    private String description;
    
    @Column(length = 2000)
    private String notes;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.ACTIVE;
    
    @Column(nullable = false)
    private Boolean completed = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;
    
    @Column(name = "due_date")
    private LocalDate dueDate;
    
    @ElementCollection
    @CollectionTable(name = "task_categories", 
                     joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "category")
    private Set<String> categories = new HashSet<>();
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    @Column(name = "completed_at")
    private Instant completedAt;
    
    @Column(name = "deleted_at")
    private Instant deletedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}

public enum TaskStatus {
    ACTIVE,
    ARCHIVED,
    DELETED
}

public enum Priority {
    LOW,
    MEDIUM,
    HIGH
}
```

#### User Entity (Simplified for MVP)

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String passwordHash;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(name = "last_login")
    private Instant lastLogin;
}
```

### 4.2 Database Schema

```sql
-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- Tasks table
CREATE TABLE tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    description VARCHAR(500) NOT NULL,
    notes TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    completed BOOLEAN NOT NULL DEFAULT false,
    priority VARCHAR(10) NOT NULL DEFAULT 'MEDIUM',
    due_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_tasks_user_id ON tasks(user_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
CREATE INDEX idx_tasks_created_at ON tasks(created_at DESC);

-- Task categories (many-to-many relationship via junction table)
CREATE TABLE task_categories (
    task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    category VARCHAR(50) NOT NULL,
    PRIMARY KEY (task_id, category)
);

CREATE INDEX idx_task_categories_task_id ON task_categories(task_id);
CREATE INDEX idx_task_categories_category ON task_categories(category);
```

### 4.3 Data Transfer Objects (DTOs)

```java
// Request DTOs
public record CreateTaskRequest(
    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description,
    
    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    String notes,
    
    Priority priority,
    
    @Future(message = "Due date must be in the future")
    LocalDate dueDate,
    
    Set<String> categories
) {}

public record UpdateTaskRequest(
    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description,
    
    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    String notes,
    
    Priority priority,
    LocalDate dueDate,
    Boolean completed,
    Set<String> categories
) {}

// Response DTOs
public record TaskResponse(
    UUID id,
    String description,
    String notes,
    TaskStatus status,
    Boolean completed,
    Priority priority,
    LocalDate dueDate,
    Set<String> categories,
    Boolean overdue,
    Instant createdAt,
    Instant updatedAt,
    Instant completedAt
) {
    public static TaskResponse from(Task task) {
        boolean overdue = task.getDueDate() != null 
            && task.getDueDate().isBefore(LocalDate.now())
            && !task.getCompleted();
        
        return new TaskResponse(
            task.getId(),
            task.getDescription(),
            task.getNotes(),
            task.getStatus(),
            task.getCompleted(),
            task.getPriority(),
            task.getDueDate(),
            task.getCategories(),
            overdue,
            task.getCreatedAt(),
            task.getUpdatedAt(),
            task.getCompletedAt()
        );
    }
}

public record TaskListResponse(
    List<TaskResponse> tasks,
    int totalCount,
    int page,
    int pageSize
) {}

// Error Response
public record ErrorResponse(
    int status,
    String error,
    String message,
    String path,
    Instant timestamp,
    String correlationId
) {}
```

---

## 5. API Design

### 5.1 RESTful Endpoints

#### Base URL
```
https://api.todoapp.com/v1
```

#### Authentication
All endpoints except `/auth/*` require JWT Bearer token:
```
Authorization: Bearer <jwt-token>
```

### 5.2 Endpoint Specifications

#### Authentication Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/register` | Register new user | No |
| POST | `/auth/login` | Login and get JWT token | No |
| POST | `/auth/refresh` | Refresh JWT token | Yes |
| POST | `/auth/logout` | Logout (invalidate token) | Yes |

#### Task Management Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/tasks` | List all tasks (with filters) | Yes |
| GET | `/tasks/{id}` | Get task by ID | Yes |
| POST | `/tasks` | Create new task | Yes |
| PUT | `/tasks/{id}` | Update task | Yes |
| PATCH | `/tasks/{id}/complete` | Toggle task completion | Yes |
| DELETE | `/tasks/{id}` | Soft delete task | Yes |
| DELETE | `/tasks/{id}/permanent` | Permanently delete task | Yes |
| POST | `/tasks/export` | Export tasks (JSON/CSV) | Yes |
| POST | `/tasks/import` | Import tasks | Yes |

#### Category Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/categories` | List all categories for user | Yes |
| GET | `/categories/{name}/tasks` | Get tasks by category | Yes |

#### Search & Filter

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/tasks/search?q={query}` | Search tasks by description/notes | Yes |

### 5.3 Query Parameters for Task Listing

**GET /tasks**

| Parameter | Type | Description | Default |
|-----------|------|-------------|---------|
| `status` | String | Filter by status (ACTIVE, ARCHIVED, DELETED) | ACTIVE |
| `completed` | Boolean | Filter by completion status | - |
| `priority` | String | Filter by priority (LOW, MEDIUM, HIGH) | - |
| `category` | String | Filter by category | - |
| `dueBefore` | Date | Filter tasks due before date | - |
| `dueAfter` | Date | Filter tasks due after date | - |
| `overdue` | Boolean | Show only overdue tasks | - |
| `sortBy` | String | Sort field (createdAt, updatedAt, dueDate, priority) | createdAt |
| `sortOrder` | String | Sort order (asc, desc) | desc |
| `page` | Integer | Page number (0-indexed) | 0 |
| `size` | Integer | Page size (max 100) | 20 |

### 5.4 Example API Requests/Responses

#### Create Task

**Request:**
```http
POST /v1/tasks
Content-Type: application/json
Authorization: Bearer <token>

{
  "description": "Complete project documentation",
  "notes": "Include API specs and architecture diagrams",
  "priority": "HIGH",
  "dueDate": "2025-11-20",
  "categories": ["Work", "Documentation"]
}
```

**Response: 201 Created**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "description": "Complete project documentation",
  "notes": "Include API specs and architecture diagrams",
  "status": "ACTIVE",
  "completed": false,
  "priority": "HIGH",
  "dueDate": "2025-11-20",
  "categories": ["Work", "Documentation"],
  "overdue": false,
  "createdAt": "2025-11-12T10:00:00Z",
  "updatedAt": "2025-11-12T10:00:00Z",
  "completedAt": null
}
```

#### List Tasks with Filters

**Request:**
```http
GET /v1/tasks?status=ACTIVE&priority=HIGH&sortBy=dueDate&sortOrder=asc&page=0&size=20
Authorization: Bearer <token>
```

**Response: 200 OK**
```json
{
  "tasks": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "description": "Complete project documentation",
      "status": "ACTIVE",
      "completed": false,
      "priority": "HIGH",
      "dueDate": "2025-11-20",
      "categories": ["Work", "Documentation"],
      "overdue": false,
      "createdAt": "2025-11-12T10:00:00Z",
      "updatedAt": "2025-11-12T10:00:00Z"
    }
  ],
  "totalCount": 1,
  "page": 0,
  "pageSize": 20
}
```

#### Error Response Example

**Response: 400 Bad Request**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Description is required",
  "path": "/v1/tasks",
  "timestamp": "2025-11-12T10:00:00Z",
  "correlationId": "abc123-def456-ghi789"
}
```

---

## 6. Security Design

### 6.1 Authentication & Authorization

**Strategy: JWT (JSON Web Token) with OAuth2**

- User registration with password hashing (BCrypt, strength 12)
- Login returns access token (JWT) + refresh token
- Access token expiry: 15 minutes
- Refresh token expiry: 7 days
- Token stored in HTTP-only cookie (preferred) or Authorization header

**JWT Claims:**
```json
{
  "sub": "user-uuid",
  "username": "john.doe",
  "email": "john@example.com",
  "roles": ["USER"],
  "iat": 1699790400,
  "exp": 1699791300
}
```

### 6.2 Security Best Practices

| Requirement | Implementation |
|-------------|----------------|
| **Password Security** | BCrypt hashing (strength 12), minimum 8 chars, complexity rules |
| **Input Validation** | Jakarta Validation on all DTOs, sanitize inputs |
| **SQL Injection Prevention** | JPA parameterized queries, no string concatenation |
| **XSS Prevention** | Content-Type enforcement, input encoding |
| **CSRF Protection** | SameSite cookies, CSRF tokens for state-changing operations |
| **CORS** | Whitelist allowed origins (frontend domain only) |
| **Rate Limiting** | 100 requests/minute per user |
| **HTTPS Only** | TLS 1.3, enforce secure connections |
| **Secrets Management** | Environment variables, AWS Secrets Manager in production |
| **Dependency Scanning** | Dependabot, Snyk integration |

### 6.3 Security Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Using JWT
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/v1/auth/**", "/actuator/health").permitAll()
                .requestMatchers("/v1/**").authenticated()
                .anyRequest().denyAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter(), 
                UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
```

---

## 7. Business Logic & Service Layer

### 7.1 Service Interfaces

```java
public interface TaskService {
    TaskResponse createTask(UUID userId, CreateTaskRequest request);
    TaskResponse getTask(UUID userId, UUID taskId);
    TaskListResponse listTasks(UUID userId, TaskFilterCriteria criteria, Pageable pageable);
    TaskResponse updateTask(UUID userId, UUID taskId, UpdateTaskRequest request);
    TaskResponse toggleCompletion(UUID userId, UUID taskId);
    void deleteTask(UUID userId, UUID taskId); // Soft delete
    void permanentlyDeleteTask(UUID userId, UUID taskId);
    List<TaskResponse> searchTasks(UUID userId, String query);
    List<String> getUserCategories(UUID userId);
    byte[] exportTasks(UUID userId, ExportFormat format);
    void importTasks(UUID userId, MultipartFile file);
}
```

### 7.2 Key Business Rules

1. **Task Creation**
   - Description is mandatory (1-500 chars)
   - Default priority: MEDIUM
   - Default status: ACTIVE
   - User ID from authenticated context

2. **Task Completion**
   - Toggle completed flag
   - Set completedAt timestamp when marking complete
   - Clear completedAt when unmarking

3. **Soft Delete**
   - Set status to DELETED
   - Set deletedAt timestamp
   - Keep data for 30 days before permanent deletion (scheduled job)

4. **Overdue Detection**
   - Task is overdue if: dueDate < today AND completed = false
   - Calculated at read time (not stored)

5. **Authorization**
   - Users can only access their own tasks
   - Check userId matches authenticated user in all operations

6. **Validation Rules**
   - Description: 1-500 characters, not blank
   - Notes: max 2000 characters
   - Due date: cannot be in the past (on creation)
   - Categories: max 20 per task, max 50 chars each

### 7.3 Transaction Management

- All service methods are `@Transactional`
- Read operations: `@Transactional(readOnly = true)`
- Write operations use default transaction settings
- Rollback on any RuntimeException

---

## 8. Non-Functional Requirements

### 8.1 Performance Requirements

| Metric | Target | Measurement |
|--------|--------|-------------|
| API Response Time (p50) | < 100ms | Micrometer metrics |
| API Response Time (p95) | < 200ms | Micrometer metrics |
| API Response Time (p99) | < 500ms | Micrometer metrics |
| Database Query Time (p95) | < 50ms | Slow query log |
| Throughput | > 1000 RPS | Load testing |
| Concurrent Users | 100+ | Load testing |
| Task List Max Size | 10,000 tasks | Pagination required |

### 8.2 Scalability

- **Horizontal Scaling:** Stateless design, can add more instances
- **Database Connection Pool:** HikariCP, 10-50 connections per instance
- **Caching Strategy:** (Future) Redis for frequently accessed data
- **Read Replicas:** (Future) For read-heavy operations

### 8.3 Reliability & Availability

| Requirement | Target | Implementation |
|-------------|--------|----------------|
| **Uptime** | 99.5% | Multi-instance deployment, health checks |
| **Data Durability** | 99.99% | PostgreSQL replication, daily backups |
| **Backup Frequency** | Daily | Automated PostgreSQL backups |
| **Recovery Time Objective (RTO)** | < 1 hour | Documented recovery procedures |
| **Recovery Point Objective (RPO)** | < 24 hours | Daily backups |
| **Error Rate** | < 1% | Error monitoring, alerting |

### 8.4 Observability

**Logging:**
- Structured JSON logging (Logback)
- Log Levels: ERROR (always), WARN (production), INFO (staging), DEBUG (dev)
- Correlation ID in all logs
- Key events: API requests, errors, security events, business operations

**Metrics (Micrometer + Prometheus):**
- HTTP request rate, duration, errors (per endpoint)
- Database query count, duration
- JVM metrics (heap, GC, threads)
- Business metrics (tasks created, completed, deleted)
- Custom metrics: overdue task count, active users

**Tracing:**
- Distributed tracing with OpenTelemetry
- Trace all external calls (database, future APIs)
- Correlation with logs via trace ID

**Health Checks:**
- `/actuator/health` - Overall health
- `/actuator/health/liveness` - Application running
- `/actuator/health/readiness` - Ready to accept traffic
- Database connectivity check
- Disk space check

### 8.5 Maintainability

- **Code Quality:** SonarQube analysis, 80% coverage, < 5% code duplication
- **Documentation:** JavaDoc for public APIs, README, API docs (OpenAPI)
- **Testing:** Unit tests (80%+), integration tests, contract tests
- **Code Style:** Google Java Format, Checkstyle enforcement
- **Dependency Management:** Regular updates, vulnerability scanning

---

## 9. Project Structure

### 9.1 Maven Multi-Module Structure

```
todo-management-backend/
├── pom.xml                          # Parent POM
├── README.md
├── .gitignore
├── docker-compose.yml
├── docs/
│   ├── adr/                         # Architecture Decision Records
│   │   ├── 0001-use-spring-boot.md
│   │   ├── 0002-use-postgresql.md
│   │   └── 0003-jwt-authentication.md
│   └── api/
│       └── openapi.yaml
├── todo-api/                        # API module (DTOs, interfaces)
│   ├── pom.xml
│   └── src/main/java/com/todoapp/api/
│       ├── dto/
│       ├── exception/
│       └── service/
├── todo-core/                       # Core business logic
│   ├── pom.xml
│   └── src/main/java/com/todoapp/core/
│       ├── domain/                  # Entities
│       ├── service/                 # Service implementations
│       └── repository/              # Repository interfaces
├── todo-app/                        # Spring Boot application
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/todoapp/
│       │   │   ├── TodoApplication.java
│       │   │   ├── config/          # Configuration classes
│       │   │   ├── controller/      # REST controllers
│       │   │   ├── security/        # Security config, filters
│       │   │   └── mapper/          # MapStruct mappers
│       │   └── resources/
│       │       ├── application.yml
│       │       ├── application-dev.yml
│       │       ├── application-prod.yml
│       │       └── db/migration/    # Flyway scripts
│       └── test/
│           └── java/com/todoapp/
│               ├── integration/
│               └── controller/
└── todo-test/                       # Test utilities
    ├── pom.xml
    └── src/main/java/com/todoapp/test/
        └── fixtures/
```

### 9.2 Package Structure (todo-app module)

```
com.todoapp
├── TodoApplication.java
├── config/
│   ├── SecurityConfig.java
│   ├── WebConfig.java
│   ├── OpenApiConfig.java
│   └── DatabaseConfig.java
├── controller/
│   ├── TaskController.java
│   ├── AuthController.java
│   └── CategoryController.java
├── security/
│   ├── JwtAuthenticationFilter.java
│   ├── JwtTokenProvider.java
│   └── UserDetailsServiceImpl.java
├── mapper/
│   ├── TaskMapper.java
│   └── UserMapper.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── TaskNotFoundException.java
│   └── UnauthorizedAccessException.java
└── util/
    └── CorrelationIdGenerator.java
```

---

## 10. Build & Deployment

### 10.1 Build Configuration

**Maven POM (Parent)**
```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <groupId>com.todoapp</groupId>
    <artifactId>todo-management-backend</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>
    
    <properties>
        <java.version>17</java.version>
        <mapstruct.version>1.5.5.Final</mapstruct.version>
        <springdoc.version>2.3.0</springdoc.version>
    </properties>
    
    <modules>
        <module>todo-api</module>
        <module>todo-core</module>
        <module>todo-app</module>
        <module>todo-test</module>
    </modules>
</project>
```

### 10.2 Dockerfile (Multi-stage)

```dockerfile
# Build stage
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY pom.xml .
COPY todo-api/ ./todo-api/
COPY todo-core/ ./todo-core/
COPY todo-app/ ./todo-app/
COPY todo-test/ ./todo-test/
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/todo-app/target/todo-app-*.jar app.jar

# Create non-root user
RUN groupadd -r todoapp && useradd -r -g todoapp todoapp
USER todoapp

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

### 10.3 Docker Compose (Development)

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: todoapp
      POSTGRES_USER: todoapp
      POSTGRES_PASSWORD: dev-password
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U todoapp"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/todoapp
      SPRING_DATASOURCE_USERNAME: todoapp
      SPRING_DATASOURCE_PASSWORD: dev-password
    depends_on:
      postgres:
        condition: service_healthy

volumes:
  postgres-data:
```

### 10.4 CI/CD Pipeline (GitHub Actions)

```yaml
name: Build and Test

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:15-alpine
        env:
          POSTGRES_DB: todoapp_test
          POSTGRES_USER: test
          POSTGRES_PASSWORD: test
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven
      
      - name: Run tests
        run: ./mvnw clean verify
        env:
          SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/todoapp_test
          SPRING_DATASOURCE_USERNAME: test
          SPRING_DATASOURCE_PASSWORD: test
      
      - name: Static Analysis
        run: ./mvnw spotbugs:check checkstyle:check
      
      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
      
      - name: Build Docker image
        run: docker build -t todoapp:${{ github.sha }} .
```

### 10.5 Deployment Strategy

**Development:**
- Deploy on every commit to `develop` branch
- Kubernetes namespace: `todoapp-dev`
- Auto-scaling: 1-3 pods

**Staging:**
- Deploy on merge to `main` branch
- Kubernetes namespace: `todoapp-staging`
- Auto-scaling: 2-5 pods
- Smoke tests after deployment

**Production:**
- Deploy via manual approval after staging validation
- Blue-Green deployment strategy
- Kubernetes namespace: `todoapp-prod`
- Auto-scaling: 3-10 pods
- Canary release: 10% → 50% → 100%

---

## 11. Testing Strategy

### 11.1 Test Pyramid

```
       ╱╲
      ╱E2E╲        10% - End-to-End Tests
     ╱────╲
    ╱ Integ╲       20% - Integration Tests
   ╱────────╲
  ╱   Unit   ╲     70% - Unit Tests
 ╱────────────╲
```

### 11.2 Unit Tests

- **Coverage Target:** > 80%
- **Framework:** JUnit 5 + Mockito
- **Scope:** Service layer, utility classes, domain logic
- **Naming:** `methodName_condition_expectedResult`

**Example:**
```java
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    
    @Mock
    private TaskRepository taskRepository;
    
    @InjectMocks
    private TaskServiceImpl taskService;
    
    @Test
    void createTask_validRequest_returnsCreatedTask() {
        // Arrange
        UUID userId = UUID.randomUUID();
        CreateTaskRequest request = new CreateTaskRequest(
            "Test task", null, Priority.MEDIUM, null, Set.of()
        );
        
        // Act
        TaskResponse response = taskService.createTask(userId, request);
        
        // Assert
        assertThat(response.description()).isEqualTo("Test task");
        verify(taskRepository).save(any(Task.class));
    }
}
```

### 11.3 Integration Tests

- **Framework:** Spring Boot Test + Testcontainers
- **Scope:** Repository layer, database queries, API endpoints
- **Database:** PostgreSQL in Docker container (Testcontainers)

**Example:**
```java
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @WithMockUser(username = "testuser")
    void createTask_validRequest_returns201() throws Exception {
        mockMvc.perform(post("/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "description": "Test task",
                      "priority": "HIGH"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.description").value("Test task"));
    }
}
```

### 11.4 Contract Tests

- **Framework:** Spring Cloud Contract
- **Purpose:** Ensure API contract compatibility with frontend
- **Scope:** Request/response schemas, status codes

### 11.5 Performance Tests

- **Tool:** JMeter / Gatling
- **Scenarios:**
  - Baseline: 50 concurrent users, 5-minute duration
  - Load test: 100 concurrent users, 1000 RPS
  - Stress test: Gradually increase to 200 users
  - Spike test: Sudden traffic increase

**Acceptance Criteria:**
- p95 response time < 200ms under normal load
- No errors under normal load
- Graceful degradation under stress

---

## 12. Operational Runbook

### 12.1 Common Operations

#### Start Application Locally
```bash
# Start dependencies
docker-compose up -d postgres

# Run application
./mvnw spring-boot:run -pl todo-app

# Access API: http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

#### Database Migration
```bash
# Flyway migrations run automatically on startup
# To run manually:
./mvnw flyway:migrate -pl todo-app

# Rollback (create undo migration manually)
./mvnw flyway:undo -pl todo-app
```

#### View Logs
```bash
# Kubernetes
kubectl logs -f deployment/todoapp -n todoapp-prod

# Docker
docker logs -f todoapp

# Local
tail -f logs/application.log
```

### 12.2 Incident Response

#### High Error Rate

**Symptoms:** Error rate > 5%, alerts firing

**Diagnosis Steps:**
1. Check application logs for exceptions
2. Check database connectivity: `kubectl exec -it <pod> -- curl localhost:8080/actuator/health`
3. Check metrics dashboard for error distribution
4. Review recent deployments

**Mitigation:**
- Rollback to previous stable version if recent deployment
- Scale up pods if CPU/memory exhausted
- Check database performance and connections

#### High Latency

**Symptoms:** p95 response time > 500ms

**Diagnosis Steps:**
1. Check database query performance (slow query log)
2. Check application metrics for slow endpoints
3. Review recent data growth or traffic patterns
4. Check for database connection pool exhaustion

**Mitigation:**
- Add database indexes if missing
- Increase connection pool size
- Scale up application pods
- Enable caching for frequent queries

#### Database Down

**Symptoms:** Health check failing, connection errors

**Diagnosis Steps:**
1. Check database pod status: `kubectl get pods -n todoapp-prod`
2. Check database logs
3. Check disk space and resources

**Mitigation:**
- Restart database pod if hung
- Restore from backup if corrupted
- Failover to read replica (if configured)

### 12.3 Monitoring & Alerts

**Prometheus Alerts:**
- High error rate (> 1%) for 5 minutes
- High latency (p95 > 500ms) for 5 minutes
- Low success rate (< 95%) for 5 minutes
- Database connection failures
- Pod crashes or restarts

**Grafana Dashboards:**
- Application Overview (RPS, latency, errors)
- Database Performance (query time, connections)
- JVM Metrics (heap, GC, threads)
- Business Metrics (tasks created, completed)

---

## 13. Architecture Decision Records (ADRs)

### ADR-0001: Use Spring Boot for Backend Framework

**Status:** Accepted  
**Date:** 2025-11-12  
**Context:**  
Need to choose a Java framework for building RESTful API. Options: Spring Boot, Quarkus, Micronaut.

**Decision:**  
Adopt Spring Boot 3.x with Java 17.

**Rationale:**
- Industry standard with extensive ecosystem
- Mature, production-ready with comprehensive documentation
- Strong community support and extensive third-party integrations
- Built-in security, data access, and web capabilities
- Team has existing expertise

**Consequences:**
- Positive: Faster development, easier hiring, reliable
- Negative: Slightly larger memory footprint vs Quarkus
- Migration: None (new project)

---

### ADR-0002: Use PostgreSQL for Primary Database

**Status:** Accepted  
**Date:** 2025-11-12  
**Context:**  
Need to choose a database for storing task data. Options: PostgreSQL, MySQL, MongoDB.

**Decision:**  
Use PostgreSQL 15.x as the primary database.

**Rationale:**
- ACID compliance ensures data integrity
- Excellent performance for read/write operations
- JSON/JSONB support for flexible data (future)
- Strong indexing and query optimization
- Open-source, no licensing costs
- Proven reliability in production

**Consequences:**
- Positive: Data integrity, performance, flexibility
- Negative: Requires database administration knowledge
- Migration: None (new project)

---

### ADR-0003: JWT for Authentication

**Status:** Accepted  
**Date:** 2025-11-12  
**Context:**  
Need stateless authentication for RESTful API. Options: Session-based, JWT, OAuth2.

**Decision:**  
Use JWT (JSON Web Tokens) with short-lived access tokens (15 min) and refresh tokens (7 days).

**Rationale:**
- Stateless: No server-side session storage required
- Scalable: Easy to scale horizontally
- Standard: Widely adopted, good library support
- Flexible: Can include custom claims
- Suitable for SPA frontend

**Consequences:**
- Positive: Scalability, simplicity, standard
- Negative: Token size in headers, refresh token management
- Security: Implement token rotation, secure storage

---

## 14. Implementation Roadmap

### 14.1 Milestones

#### Milestone 1: Project Setup & Foundation (Week 1)
**Goal:** Development environment and project structure ready

- [x] Initialize Maven multi-module project
- [x] Configure Spring Boot 3.x with Java 17
- [x] Set up PostgreSQL with Docker Compose
- [x] Configure Flyway for database migrations
- [x] Implement basic security configuration
- [x] Set up CI/CD pipeline (GitHub Actions)
- [x] Configure code quality tools (Checkstyle, SpotBugs)

**Deliverable:** Runnable application skeleton

---

#### Milestone 2: Core Task Management (Week 2-3)
**Goal:** MVP task CRUD operations

**Sprint 1: Basic CRUD (Week 2)**
- [x] Implement User entity and authentication
- [x] Implement Task entity and repository
- [x] Create TaskService with CRUD operations
- [x] Implement TaskController endpoints (POST, GET, PUT, DELETE)
- [x] Add input validation
- [x] Write unit tests (>70% coverage)
- [x] Add integration tests

**Sprint 2: Completion & Persistence (Week 3)**
- [x] Implement task completion toggle
- [x] Add soft delete functionality
- [x] Implement task listing with pagination
- [x] Add OpenAPI documentation
- [x] Deploy to staging environment

**Deliverable:** Functional TODO API with basic operations

---

#### Milestone 3: Enhanced Features (Week 4-5)
**Goal:** Task organization and usability features

**Sprint 3: Prioritization & Filtering (Week 4)**
- [x] Add priority field to Task entity
- [x] Implement priority-based filtering
- [x] Add task filtering (status, completion, priority)
- [x] Implement sorting (createdAt, dueDate, priority)
- [x] Write tests for new features

**Sprint 4: Due Dates & Categories (Week 5)**
- [x] Add due date functionality
- [x] Implement overdue detection
- [x] Add task categories/tags
- [x] Implement category-based filtering
- [x] Update API documentation

**Deliverable:** Enhanced TODO API with organization features

---

#### Milestone 4: Advanced Features & Polish (Week 6-7)
**Goal:** Search, export, and production readiness

**Sprint 5: Search & Export (Week 6)**
- [x] Implement full-text search
- [x] Add export functionality (JSON, CSV)
- [x] Add import functionality
- [x] Optimize query performance

**Sprint 6: Observability & Production Prep (Week 7)**
- [x] Configure structured logging
- [x] Add custom metrics (Micrometer)
- [x] Implement health checks
- [x] Set up monitoring dashboards (Grafana)
- [x] Configure alerts (Prometheus)
- [x] Performance testing and optimization
- [x] Security audit and fixes
- [x] Production deployment

**Deliverable:** Production-ready TODO Management API

---

### 14.2 Risk Assessment & Mitigation

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| **Database Performance Issues** | Medium | High | Add appropriate indexes, optimize queries, implement caching |
| **Security Vulnerabilities** | Medium | Critical | Regular dependency scanning, security audit, penetration testing |
| **Authentication Complexity** | Low | Medium | Use established libraries (Spring Security), follow best practices |
| **Scalability Bottlenecks** | Low | High | Stateless design, horizontal scaling, load testing early |
| **Data Loss** | Low | Critical | Implement automated backups, test restore procedures |
| **Third-party Dependency Issues** | Medium | Medium | Pin dependency versions, monitor advisories, have fallback plans |

---

## 15. Acceptance Criteria & Definition of Done

### 15.1 API Acceptance Criteria

For each endpoint implementation:

✅ **Functional Requirements**
- Endpoint responds with correct status codes
- Request/response payloads match OpenAPI spec
- Validation rules are enforced
- Business logic is correctly implemented
- Authorization checks are in place

✅ **Quality Requirements**
- Unit tests written (>80% coverage)
- Integration tests written
- API documentation is complete
- Code review approved
- No critical security vulnerabilities

✅ **Non-Functional Requirements**
- Response time meets SLA (<200ms p95)
- Error handling is comprehensive
- Logging includes correlation IDs
- Metrics are instrumented

### 15.2 Definition of Done (Feature)

A feature is considered done when:

1. ✅ All acceptance criteria met
2. ✅ Code written and reviewed
3. ✅ Unit tests written (>80% line coverage)
4. ✅ Integration tests written
5. ✅ API documentation updated
6. ✅ Database migrations created and tested
7. ✅ Manual testing completed
8. ✅ No critical/high severity bugs
9. ✅ Performance requirements met
10. ✅ Security scan passed (no critical issues)
11. ✅ Merged to main branch
12. ✅ Deployed to staging environment

### 15.3 Production Readiness Checklist

Before production deployment:

**Code Quality**
- [ ] Test coverage > 80%
- [ ] No critical/high bugs
- [ ] Code review completed
- [ ] Static analysis passed (SonarQube)

**Security**
- [ ] Dependency vulnerability scan passed
- [ ] Security audit completed
- [ ] Secrets not hardcoded
- [ ] HTTPS enforced
- [ ] Authentication/authorization tested

**Documentation**
- [ ] API documentation complete (OpenAPI)
- [ ] README updated
- [ ] Operational runbook created
- [ ] Architecture diagrams current

**Operations**
- [ ] Health checks configured
- [ ] Monitoring dashboards created
- [ ] Alerts configured
- [ ] Logging tested
- [ ] Backup/restore procedures tested
- [ ] Rollback plan documented

**Performance**
- [ ] Load testing passed
- [ ] Database indexes optimized
- [ ] Query performance validated
- [ ] Resource limits configured

**Deployment**
- [ ] CI/CD pipeline tested
- [ ] Blue-green deployment configured
- [ ] Database migrations tested
- [ ] Smoke tests automated
- [ ] Rollback tested

---

## 16. Appendix

### 16.1 Glossary

- **JWT:** JSON Web Token - compact, URL-safe token format for authentication
- **DTO:** Data Transfer Object - object that carries data between processes
- **CRUD:** Create, Read, Update, Delete - basic data operations
- **ORM:** Object-Relational Mapping - technique for converting data between systems
- **SLA:** Service Level Agreement - commitment to service availability/performance
- **RTO:** Recovery Time Objective - target time for service restoration
- **RPO:** Recovery Point Objective - maximum acceptable data loss timeframe
- **Soft Delete:** Marking records as deleted without physical removal

### 16.2 References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [OpenAPI Specification](https://swagger.io/specification/)
- [12-Factor App Methodology](https://12factor.net/)
- [REST API Best Practices](https://restfulapi.net/)

### 16.3 Contact Information

- **Technical Lead:** [Name]
- **Product Owner:** [Name]
- **DevOps Lead:** [Name]
- **Project Repository:** https://github.com/organization/todo-management-backend
- **Documentation:** https://docs.todoapp.com
- **Support:** support@todoapp.com

---

## Document Change History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2025-11-12 | Backend Architect | Initial technical specification |

---

**Document Status:** Draft for Review  
**Next Review Date:** 2025-11-19  
**Approval Required From:** Technical Lead, Product Owner, Security Team  

---

*End of Technical Specification*
