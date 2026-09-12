# HeartMate — Matrimonial Platform Backend Documentation

## Complete Technical Reference Guide

**Version:** 1.0.0  
**Framework:** Spring Boot 3.2.0  
**Language:** Java 21  
**ORM:** Spring Data JPA / Hibernate  
**Database:** MySQL 8.x  
**Authentication:** Session-based (Spring Security)  
**API Docs:** OpenAPI 3.0 (Swagger UI)  

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Technology Stack & Dependencies](#2-technology-stack--dependencies)
3. [Maven Build System (pom.xml)](#3-maven-build-system-pomxml)
4. [Application Configuration (application.properties)](#4-application-configuration-applicationproperties)
5. [Spring Boot Entry Point (MatrimonialApplication.java)](#5-spring-boot-entry-point-matrimonialapplicationjava)
6. [Security Configuration (SecurityConfig.java)](#6-security-configuration-securityconfigjava)
7. [OpenAPI Configuration (OpenApiConfig.java)](#7-openapi-configuration-openapiconfigjava)
8. [Entity Layer](#8-entity-layer)
   - 8.1 [User Entity](#81-user-entity)
   - 8.2 [Profile Entity](#82-profile-entity)
   - 8.3 [Interest Entity](#83-interest-entity)
9. [Data Transfer Objects (DTOs)](#9-data-transfer-objects-dtos)
   - 9.1 [AuthResponse](#91-authresponse)
   - 9.2 [LoginRequest](#92-loginrequest)
   - 9.3 [RegisterRequest](#93-registerrequest)
   - 9.4 [SendInterestRequest](#94-sendinterestrequest)
   - 9.5 [ProfileRequest](#95-profilerequest)
10. [Repository Layer](#10-repository-layer)
    - 10.1 [UserRepository](#101-userrepository)
    - 10.2 [ProfileRepository](#102-profilerepository)
    - 10.3 [InterestRepository](#103-interestrepository)
11. [Service Layer](#11-service-layer)
    - 11.1 [AuthService](#111-authservice)
    - 11.2 [UserService](#112-userservice)
    - 11.3 [ProfileService](#113-profileservice)
    - 11.4 [InterestService](#114-interestservice)
12. [Controller Layer](#12-controller-layer)
    - 12.1 [AuthController](#121-authcontroller)
    - 12.2 [UserController](#122-usercontroller)
    - 12.3 [ProfileController](#123-profilecontroller)
    - 12.4 [InterestController](#124-interestcontroller)
13. [Exception Handling Architecture](#13-exception-handling-architecture)
14. [Database Schema Design](#14-database-schema-design)
15. [Seed Data (data.sql)](#15-seed-data-datasql)
16. [Complete API Reference](#16-complete-api-reference)
17. [Authentication Flow Deep Dive](#17-authentication-flow-deep-dive)
18. [Design Patterns & Architectural Decisions](#18-design-patterns--architectural-decisions)
19. [CORS Configuration Explained](#19-cors-configuration-explained)
20. [Database Relationships & Cascade Behavior](#20-database-relationships--cascade-behavior)
21. [Bean Validation Deep Dive](#21-bean-validation-deep-dive)
22. [Transaction Management](#22-transaction-management)
23. [Entities vs DTOs — Mass Assignment Protection](#23-entities-vs-dtos--mass-assignment-protection)
24. [JPQL Custom Queries vs Method-Name Queries](#24-jpql-custom-queries-vs-method-name-queries)
25. [Running & Testing the Application](#25-running--testing-the-application)

---

## 1. Project Overview

HeartMate is a matrimonial platform built as a full-stack web application. The backend is a Java Spring Boot REST API that provides user registration, authentication, profile management, and an interest/proposal system — the core features of any matrimonial service. The frontend is a Next.js pixel-art themed matrimonial site running on `localhost:3000`.

The backend follows a classic **layered architecture**:

```
Controller Layer → Service Layer → Repository Layer → Database
```

Each layer has a single responsibility. Controllers handle HTTP request/response mapping. Services contain business logic. Repositories interface with the database through Spring Data JPA. Entities represent database tables as Java objects.

The system uses **session-based authentication** rather than JWT tokens. When a user logs in, Spring Security creates an HTTP session stored in the server's memory, and the session ID is communicated to the client via a `JSESSIONID` cookie. This is a simpler, more traditional approach that works well for monolithic applications where the frontend and backend run on the same machine or within the same domain.

The project seeds itself with 13 test users, 13 profiles, and 6 interest relationships on every startup, making it immediately useful for development and demonstration without manual data entry.

---

## 2. Technology Stack & Dependencies

### Core Framework

| Dependency | Artifact | Purpose |
|---|---|---|
| Spring Boot Starter Web | `spring-boot-starter-web` | Provides the embedded Tomcat server, Spring MVC, and REST controller support. This is the foundation for building RESTful HTTP APIs. |
| Spring Boot Starter Data JPA | `spring-boot-starter-data-jpa` | Integrates Spring Data JPA with Hibernate as the default JPA provider. Provides repository abstractions, query derivation, and entity lifecycle management. |
| Spring Boot Starter Security | `spring-boot-starter-security` | Adds Spring Security to the application, providing authentication, authorization, session management, CSRF protection (disabled here), and password encoding. |
| Spring Boot Starter Validation | `spring-boot-starter-validation` | Integrates Jakarta Bean Validation (Hibernate Validator) for declarative input validation using annotations like `@NotBlank`, `@Email`, `@Min`, `@Max`, `@Size`. |

### Database

| Dependency | Artifact | Purpose |
|---|---|---|
| MySQL Connector/J | `mysql-connector-j` | The official JDBC driver for MySQL. Placed at `runtime` scope because it is only needed at runtime — the application code never directly references MySQL driver classes. |

### API Documentation

| Dependency | Artifact | Purpose |
|---|---|---|
| SpringDoc OpenAPI | `springdoc-openapi-starter-webmvc-ui` (v2.3.0) | Generates OpenAPI 3.0 documentation from the controller annotations and provides an embedded Swagger UI at `/swagger-ui.html` for interactive API exploration. |

### Testing

| Dependency | Artifact | Purpose |
|---|---|---|
| Spring Boot Starter Test | `spring-boot-starter-test` | Includes JUnit 5, Mockito, Spring Test, and AssertJ for unit and integration testing. Available at `test` scope. |

### Java Version

The project targets **Java 21** (LTS), configured via three Maven compiler properties:

- `maven.compiler.source=21` — Source compatibility
- `maven.compiler.target=21` — Target bytecode compatibility
- `maven.compiler.release=21` — Combined source + target + bootstrap classpath

Java 21 brings virtual threads (Project Loom), pattern matching for switch, record patterns, and other modern language features, though this project primarily uses traditional Java patterns appropriate for an internship-level codebase.

---

## 3. Maven Build System (pom.xml)

The `pom.xml` is Maven's Project Object Model — the declarative build configuration for the entire project.

### Parent POM

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
    <relativePath/>
</parent>
```

The Spring Boot parent POM provides several critical benefits:

1. **Dependency Management:** Declares compatible versions for hundreds of Spring ecosystem and third-party libraries. When you add `spring-boot-starter-web` without a `<version>` tag, the parent ensures version 3.2.0 is used, along with compatible versions of Jackson, Tomcat, SLF4J, Logback, and dozens of other transitive dependencies.

2. **Plugin Management:** Pre-configures the `spring-boot-maven-plugin` and `maven-compiler-plugin` with sensible defaults.

3. **Resource Filtering:** Enables filtering of `application.properties` and other resource files to replace Maven property placeholders at build time.

4. **Default Configuration:** Sets UTF-8 encoding, Java source/target levels, and other build conventions.

The `<relativePath/>` (empty) tells Maven to look in the local repository (`~/.m2/repository`) for the parent POM rather than searching the filesystem.

### Build Plugins

**maven-compiler-plugin (3.14.0):** Explicitly configured to compile with Java 21 source and target. While the parent POM sets these properties, the explicit plugin declaration overrides the version to 3.14.0 for access to the latest compiler features and bug fixes.

**spring-boot-maven-plugin:** Provides the `mvn spring-boot:run` command for development and creates an executable fat JAR (`matrimonial-jpa-backend-1.0.0.jar`) when running `mvn package`. The fat JAR includes all dependencies (embedded Tomcat, Hibernate, etc.) and can be run with `java -jar`.

### Maven Lifecycle Commands

| Command | Purpose |
|---|---|
| `mvn clean` | Deletes the `target/` directory |
| `mvn compile` | Compiles Java source files |
| `mvn spring-boot:run` | Starts the application with hot-reload |
| `mvn package` | Creates the executable JAR |
| `mvn test` | Runs JUnit test suites |

---

## 4. Application Configuration (application.properties)

The `application.properties` file configures Spring Boot's auto-configuration. Every property here overrides a default value that Spring Boot would otherwise use.

### Server Configuration

```properties
server.port=8080
```

Sets the embedded Tomcat HTTP port to 8080. Without this, Spring Boot defaults to 8080 as well, but making it explicit documents the contract. The Next.js frontend communicates with this port via `http://localhost:8080/api/...`.

### Database Configuration

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/matrimonial_jpa_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

**URL Parameters Explained:**

- `createDatabaseIfNotExist=true` — Automatically creates the `matrimonial_jpa_db` database if it does not exist. This eliminates manual database setup steps.
- `useSSL=false` — Disables SSL for the database connection. Acceptable for local development; should be enabled in production.
- `serverTimezone=UTC` — Forces the JVM to use UTC when communicating with MySQL, preventing timezone mismatch issues.
- `allowPublicKeyRetrieval=true` — Required for the `caching_sha2_password` authentication plugin used by MySQL 8.0+. Without this, the JDBC driver cannot retrieve the server's public key for RSA-based password exchange.

The `com.mysql.cj.jdbc.Driver` class is the MySQL Connector/J 8.x driver (the `cj` package distinguishes it from the legacy 5.x driver).

### JPA / Hibernate Configuration

```properties
spring.jpa.hibernate.ddl-auto=update
```

The `ddl-auto` property controls Hibernate's DDL (Data Definition Language) strategy:

- **`update`** — Hibernate compares the entity definitions with the existing database schema and generates `ALTER TABLE` statements to add missing columns, tables, or constraints. It never drops columns or tables, making it safe for development with existing data.
- **`none`** — No DDL manipulation (recommended for production).
- **`create`** — Drops and recreates the entire schema on every startup.
- **`create-drop`** — Creates on startup, drops on shutdown.

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

`show-sql=true` logs all SQL statements to stdout. `format_sql=true` pretty-prints them with indentation and line breaks for readability.

```properties
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

Tells Hibernate to generate SQL syntax specific to MySQL. This affects date/time functions, limit clauses, quoting behavior, and other dialect-specific SQL features.

### Data Initialization

```properties
spring.jpa.defer-datasource-initialization=true
spring.sql.init.mode=always
spring.sql.init.continue-on-error=true
```

- **`defer-datasource-initialization=true`** — Ensures Hibernate runs its DDL scripts *before* Spring runs `data.sql`. Without this, `data.sql` would execute before the tables exist, causing errors.
- **`sql.init.mode=always`** — Runs `data.sql` on every application startup. Use `mode=embedded` to only run with embedded databases, or `mode=never` to disable.
- **`sql.init.continue-on-error=true`** — If a SQL statement in `data.sql` fails (e.g., duplicate key), execution continues rather than aborting. This is essential for seed data that uses `INSERT IGNORE`.

### Session Configuration

```properties
spring.session.store-type=none
```

Disables Spring Session's external store (Redis, JDBC, etc.). Sessions are stored in the server's in-memory `HttpSession` by default. This is appropriate for development and single-instance deployments but would need to be replaced with Redis or JDBC sessions for production with multiple server instances.

---

## 5. Spring Boot Entry Point (MatrimonialApplication.java)

```java
@SpringBootApplication
public class MatrimonialApplication {
    public static void main(String[] args) {
        SpringApplication.run(MatrimonialApplication.class, args);
        System.out.println("Matrimonial JPA Backend running at http://localhost:8080");
    }
}
```

### The `@SpringBootApplication` Annotation

This is a convenience annotation that combines three separate annotations:

1. **`@Configuration`** — Marks the class as a source of bean definitions for the Spring IoC (Inversion of Control) container. Spring scans this class and its package for `@Bean` methods and `@Component` classes.

2. **`@EnableAutoConfiguration`** — Triggers Spring Boot's auto-configuration mechanism. Based on the classpath dependencies (MySQL connector, JPA starter, security starter), Spring Boot automatically configures:
   - A `DataSource` bean pointing to the MySQL database
   - A `LocalContainerEntityManagerFactoryBean` for JPA/Hibernate
   - A `PlatformTransactionManager` for declarative transactions
   - A `SecurityFilterChain` for HTTP security
   - An embedded Tomcat server

3. **`@ComponentScan`** — Scans the `com.matrimonial` package and all sub-packages for Spring-managed components: `@Service`, `@Controller`, `@Repository`, `@Configuration`, `@Component`.

### Application Startup Sequence

1. `SpringApplication.run()` boots the Spring application context
2. Auto-configuration detects MySQL on the classpath, creates the `DataSource`
3. Hibernate scans entities in `com.matrimonial.entity`, generates DDL
4. `data.sql` is executed to seed test data
5. All `@Service`, `@Controller`, and `@Configuration` beans are instantiated
6. Spring Security filter chain is constructed
7. Embedded Tomcat starts on port 8080
8. The startup message is printed to the console

---

## 6. Security Configuration (SecurityConfig.java)

This is one of the most critical configuration classes in the application. It defines how Spring Security handles authentication, authorization, CORS, and session management.

### Password Encoder Bean

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

Creates a `BCryptPasswordEncoder` bean that is injected into `AuthService` and `UserService`. BCrypt is an adaptive hashing function based on the Blowfish cipher. Key properties:

- **One-way:** Passwords cannot be decrypted — only compared
- **Salted:** Each password gets a unique random salt (embedded in the hash output)
- **Adaptive:** The cost factor (work factor) can be increased over time to maintain security against Moore's Law
- **Output format:** `$2a$10$` prefix indicates BCrypt with cost factor 10. The resulting hash is 60 characters.

Example BCrypt hash from `data.sql`: `$2b$10$TpVRCNsQ1jAhSlvHmueDh.qp9Kft0gYUJoezwuYT6qOmUXcZ1KJa.`

### Security Filter Chain

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .securityContext(context -> context
            .securityContextRepository(new HttpSessionSecurityContextRepository())
        )
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/logout").permitAll()
            .requestMatchers("/api/auth/me").authenticated()
            .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
            .requestMatchers("/error").permitAll()
            .requestMatchers("/api/**").authenticated()
            .anyRequest().permitAll()
        );
    return http.build();
}
```

**CSRF Disabled:** Cross-Site Request Forgery protection is disabled because this API is consumed by a separate Next.js frontend application. CSRF protection is designed for same-origin form submissions where cookies are automatically included. Since the frontend runs on `localhost:3000` and the backend on `localhost:8080` (different origins), and the frontend manages its own state, CSRF tokens add complexity without proportional security benefit in this development setup. **In production**, CSRF should be enabled with token-based protection.

**Security Context Repository:** `HttpSessionSecurityContextRepository` stores the `SecurityContext` (containing the authenticated user's identity) in the HTTP session. This means once a user logs in, their authentication persists across requests within the same session without re-authenticating.

**Authentication Entry Point:** When an unauthenticated request hits a protected endpoint, Spring Security returns HTTP 401 (Unauthorized) instead of redirecting to a login page. This is essential for REST APIs that expect JSON error responses rather than HTML login forms.

**Authorization Rules (evaluated top-to-bottom):**

| Rule | Effect |
|---|---|
| `OPTIONS /** → permitAll` | Allows preflight CORS requests from any origin |
| `/api/auth/register, /login, /logout → permitAll` | Public auth endpoints — anyone can register, login, logout |
| `/api/auth/me → authenticated` | Requires an active session to check current user |
| `/swagger-ui/**, /v3/api-docs/** → permitAll` | API documentation is publicly accessible |
| `/error → permitAll` | Spring Boot error page is accessible |
| `/api/** → authenticated` | All other API endpoints require authentication |
| `anyRequest → permitAll` | Everything else (static resources, etc.) is public |

The order matters: Spring Security evaluates rules from top to bottom and uses the **first matching rule**. So `/api/auth/login` matches the `permitAll` rule before reaching the `/api/** → authenticated` rule.

### CORS Configuration

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:3001"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

This configuration enables Cross-Origin Resource Sharing (CORS) so the Next.js frontend at `localhost:3000` can make API requests to the backend at `localhost:8080`. Without this, the browser's Same-Origin Policy would block cross-origin requests.

- **Allowed Origins:** `localhost:3000` (primary frontend) and `localhost:3001` (likely a secondary development server)
- **Allowed Methods:** All standard HTTP methods including OPTIONS (for preflight)
- **Allowed Headers:** `*` — Accepts any headers the client sends
- **Allow Credentials:** `true` — Allows cookies (`JSESSIONID`) to be sent cross-origin. **This is critical** for session-based auth to work across different ports.
- **Max Age:** 3600 seconds (1 hour) — How long the browser caches preflight responses

---

## 7. OpenAPI Configuration (OpenApiConfig.java)

```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI matrimonialOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Matrimonial Management System API")
                        .version("1.0.0")
                        .description("Basic internship-level Matrimonial Website REST API..."));
    }
}
```

This configuration bean customizes the auto-generated OpenAPI 3.0 specification. SpringDoc OpenAPI scans all `@RestController` classes, their `@RequestMapping` annotations, request/response DTOs, and validation constraints to generate a complete API specification.

**Access Points:**
- **Swagger UI:** `http://localhost:8080/swagger-ui.html` — Interactive API explorer with "Try It Out" functionality
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs` — Machine-readable API specification

The Swagger UI allows developers and testers to explore all endpoints, see request/response schemas, and execute API calls directly from the browser without external tools like Postman.

---

## 8. Entity Layer

Entities are Java classes annotated with JPA annotations that map to database tables. Each entity instance represents a row in the corresponding table.

### 8.1 User Entity

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Profile profile;
```

**Annotations Breakdown:**

- **`@Entity`** — Marks this class as a JPA entity. Hibernate will create a table for it and manage its persistence.
- **`@Table(name = "users")`** — Explicitly names the database table `users`. Without this, Hibernate would default to the class name `user` (which is a reserved keyword in MySQL).
- **`@Id`** — Marks `userId` as the primary key.
- **`@GeneratedValue(strategy = GenerationType.IDENTITY)`** — Uses MySQL's `AUTO_INCREMENT` feature. The database generates the ID value when a new row is inserted. Hibernate retrieves the generated ID via `getGeneratedKeys()`.
- **`@Column(name = "user_id")`** — Maps the Java field to the `user_id` column. Without this, the column would be named `user_id` by Hibernate's snake_case naming strategy anyway, but making it explicit prevents surprises if the naming strategy changes.
- **`@NotBlank`** — Jakarta Bean Validation: the field must not be null, empty, or whitespace-only.
- **`@Email`** — Jakarta Bean Validation: the field must match a valid email format.
- **`@Column(nullable = false, unique = true)`** — DDL constraint: the column is `NOT NULL` and has a `UNIQUE` index.
- **`@JsonIgnore`** on `password` — Prevents the password hash from being serialized to JSON in API responses. This is a security measure — even if an endpoint returns a User object, the password is never exposed.
- **`@JsonIgnore`** on `profile` — Prevents infinite recursion in JSON serialization. Since `Profile` has a `User` reference and `User` has a `Profile` reference, serializing one would create an infinite loop. The profile is accessed through dedicated profile endpoints instead.
- **`@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)`** — Defines the inverse (non-owning) side of the one-to-one relationship with Profile. `mappedBy = "user"` means the `Profile` entity owns the foreign key. `cascade = CascadeType.ALL` means operations like persist, merge, remove, and refresh cascade from User to Profile.

**Design Decision — Why User and Profile are Separate:**

Separating authentication data (User) from profile data (Profile) follows the **Single Responsibility Principle**. User handles identity and credentials. Profile handles matrimonial attributes. This allows:
- Creating a User account during registration before the profile is complete
- The `hasProfile` flag in `AuthResponse` tells the frontend whether to show the profile creation form or the profile view
- Profile can be updated independently of authentication concerns
- Profile can be deleted without losing the account

### 8.2 Profile Entity

```java
@Entity
@Table(name = "profiles")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String city;

    private String education;
    private String occupation;

    @Column(length = 1000)
    private String about;
```

**Key Annotations:**

- **`@JsonIgnoreProperties(ignoreUnknown = true)`** — When deserializing JSON to Profile, any unknown properties in the JSON payload are silently ignored. This prevents `UnrecognizedPropertyException` when the frontend sends extra fields.
- **`@OneToOne`** — Defines the owning side of the one-to-one relationship with User.
- **`@JoinColumn(name = "user_id", nullable = false, unique = true)`** — Creates a `user_id` foreign key column in the `profiles` table. The `unique = true` constraint ensures one profile per user. The `nullable = false` constraint means every profile must be linked to a user.
- **`@Column(length = 1000)`** — Limits the `about` column to 1000 characters in the database schema. This matches the `@Size(max = 1000)` validation on `ProfileRequest`.

**Fields:**

| Field | Type | Required | Constraint | Purpose |
|---|---|---|---|---|
| `profileId` | Long | Auto | PK, Auto Increment | Unique profile identifier |
| `user` | User | Yes | FK, Unique, Not Null | Links to the owning user account |
| `age` | Integer | Yes | Not Null | User's age (validated 18-99 at DTO level) |
| `gender` | String | Yes | Not Null | "Male" or "Female" |
| `city` | String | Yes | Not Null | User's city (max 100 chars via DTO) |
| `education` | String | No | Nullable | Educational qualification |
| `occupation` | String | No | Nullable | Professional occupation |
| `about` | String | No | Max 1000 chars | Self-description for matrimonial bio |

### 8.3 Interest Entity

```java
@Entity
@Table(name = "interests")
public class Interest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interest_id")
    private Long interestId;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(nullable = false)
    private String status;
```

**Key Annotations:**

- **`@ManyToOne`** on `sender` and `receiver` — Both the sender and receiver are User entities. Many interests can have the same sender or receiver. This creates two foreign key columns in the `interests` table.
- **`@JoinColumn(name = "sender_id", nullable = false)`** — The `sender_id` foreign key column references `users.user_id`. It cannot be null.
- **`status` field** — A simple String that holds one of three values: `"PENDING"`, `"ACCEPTED"`, or `"REJECTED"`. While an enum would be more type-safe, using a String is simpler for an internship-level project and avoids the need for `@Enumerated` annotation and custom database types.

**Interest Lifecycle:**

1. User A sends interest to User B → status = `PENDING`
2. User B accepts → status = `ACCEPTED`
3. Or User B rejects → status = `REJECTED`
4. Either party can delete the interest record entirely

---

## 9. Data Transfer Objects (DTOs)

DTOs are plain Java objects used to transfer data between layers. They decouple the internal entity representation from the external API contract.

### 9.1 AuthResponse

```java
public class AuthResponse {
    private Long userId;
    private String name;
    private String email;
    private boolean hasProfile;
    private Long profileId;
```

Returned by login, register, and the `/me` endpoint. Contains only the information the frontend needs:

- `userId` — For subsequent API calls that reference the user
- `name` — Display name for the UI
- `email` — User's email
- `hasProfile` — Boolean flag telling the frontend whether to show profile creation or profile view
- `profileId` — Null if no profile exists; the profile ID if one exists

**Why not return the full User entity?** The full User entity includes the BCrypt password hash. Even though `@JsonIgnore` prevents serialization, using a DTO is more intentional and avoids relying on a single annotation for security. DTOs also allow shaping the response exactly to the frontend's needs without over- or under-fetching.

### 9.2 LoginRequest

```java
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
```

Accepts email and password for login. The `@NotBlank` and `@Email` annotations are processed by Spring's `@Valid` annotation on the controller method parameter. If validation fails, a `MethodArgumentNotValidException` is thrown and handled by the `GlobalExceptionHandler`.

### 9.3 RegisterRequest

```java
public class RegisterRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }
```

Requires password confirmation to prevent typos during registration. The `isPasswordMatching()` method is called in `AuthService.register()` and throws `IllegalArgumentException` if the passwords don't match. This is business logic validation, not structural validation, so it's checked programmatically rather than via annotations.

### 9.4 SendInterestRequest

```java
public class SendInterestRequest {
    @NotNull(message = "Sender ID is required")
    private Long senderId;

    @NotNull(message = "Receiver ID is required")
    private Long receiverId;
```

Simple DTO for the interest-sending endpoint. Both IDs are required (non-null). The controller extracts these values and passes them to `InterestService.sendInterest()`.

### 9.5 ProfileRequest

```java
public class ProfileRequest {
    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be 18 or above")
    @Max(value = 99, message = "Age must be 99 or below")
    private Integer age;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must be at most 100 characters")
    private String city;

    @Size(max = 200, message = "Education must be at most 200 characters")
    private String education;

    @Size(max = 200, message = "Occupation must be at most 200 characters")
    private String occupation;

    @Size(max = 1000, message = "About must be at most 1000 characters")
    private String about;
```

Used for both creating and updating profiles. The validation constraints enforce:

- Age must be between 18 and 99 (legal age for matrimonial services)
- Gender, city, and age are required
- City limited to 100 characters
- Education and occupation limited to 200 characters
- About section limited to 1000 characters

**Why age 18-99?** The `@Min(18)` enforces the minimum legal age for marriage-related activities. `@Max(99)` is a practical upper bound for the matrimonial platform.

---

## 10. Repository Layer

Repositories extend `JpaRepository`, which provides CRUD operations and pagination out of the box. Spring Data JPA automatically implements the interface at runtime.

### 10.1 UserRepository

```java
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);
    Optional<User> findByEmailIgnoreCase(String email);
}
```

**Method Name Query Derivation:**

Spring Data JPA parses method names to generate JPQL queries automatically:

| Method | Generated Query |
|---|---|
| `existsByEmail(String email)` | `SELECT COUNT(u) > 0 FROM User u WHERE u.email = ?1` |
| `findByEmail(String email)` | `SELECT u FROM User u WHERE u.email = ?1` |
| `existsByEmailIgnoreCase(String email)` | `SELECT COUNT(u) > 0 FROM User u WHERE LOWER(u.email) = LOWER(?1)` |
| `findByEmailIgnoreCase(String email)` | `SELECT u FROM User u WHERE LOWER(u.email) = LOWER(?1)` |

**Why case-insensitive variants?** The `AuthService` normalizes emails to lowercase (`email.trim().toLowerCase()`), but the case-insensitive queries provide defense-in-depth. If a user registers with `User@Example.COM` and later tries to log in with `user@example.com`, both will match the same record.

**`Optional<User>`:** Spring Data returns `Optional` for single-entity finders. This forces the caller to explicitly handle the "not found" case rather than receiving a null reference and risking a NullPointerException.

### 10.2 ProfileRepository

```java
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserUserId(Long userId);

    @Query("SELECT p FROM Profile p JOIN p.user u WHERE " +
           "(:gender IS NULL OR :gender = '' OR LOWER(p.gender) = LOWER(:gender)) AND " +
           "(:city IS NULL OR :city = '' OR LOWER(p.city) = LOWER(:city)) AND " +
           "(:age IS NULL OR p.age = :age)")
    List<Profile> searchProfiles(@Param("gender") String gender,
                                 @Param("city") String city,
                                 @Param("age") Integer age);
}
```

**`findByUserUserId(Long userId)`:** Follows the JPA path expression syntax: `Profile → user → userId`. This finds a profile by its associated user's ID.

**`searchProfiles` — Custom JPQL Query:**

This is a dynamic search query with optional parameters. Each condition follows the pattern:

```
(:parameter IS NULL OR :parameter = '' OR entity.field = :parameter)
```

This means: "If the parameter is null or empty, skip this filter; otherwise, apply it." This allows the frontend to search by any combination of gender, city, and age — or all three, or none (returning all profiles).

The `JOIN p.user u` is technically not needed since `gender`, `city`, and `age` are on the Profile table directly. The join would be needed if the query filtered by user attributes like `name`.

The `@Param` annotation maps method parameters to named JPQL parameters (`:gender`, `:city`, `:age`).

### 10.3 InterestRepository

```java
public interface InterestRepository extends JpaRepository<Interest, Long> {
    List<Interest> findBySenderUserId(Long senderId);
    List<Interest> findByReceiverUserId(Long receiverId);
    boolean existsBySenderUserIdAndReceiverUserIdAndStatus(Long senderId, Long receiverId, String status);
    void deleteBySenderUserIdOrReceiverUserId(Long senderId, Long receiverId);
}
```

**Query Methods:**

| Method | Purpose |
|---|---|
| `findBySenderUserId(Long)` | All interests sent by a user (outgoing) |
| `findByReceiverUserId(Long)` | All interests received by a user (incoming) |
| `existsBySenderUserIdAndReceiverUserIdAndStatus(...)` | Checks if a pending interest already exists (duplicate prevention) |
| `deleteBySenderUserIdOrReceiverUserId(...)` | Deletes all interests involving a user (used during user deletion) |

The `deleteBy` prefix tells Spring Data to generate a `DELETE` query rather than a `SELECT` query. This is executed as a bulk delete statement rather than loading entities into memory.

---

## 11. Service Layer

Services contain the business logic that sits between controllers and repositories. They orchestrate multiple repository calls, enforce business rules, and manage transactions.

### 11.1 AuthService

```java
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
```

**Registration Flow (`register` method):**

1. Validate passwords match (`request.isPasswordMatching()`)
2. Normalize email: trim whitespace, convert to lowercase
3. Check for duplicate email (case-insensitive)
4. Create User entity with name, normalized email, BCrypt-hashed password
5. Save to database
6. Set authentication in Spring Security context
7. Return AuthResponse with user details

**Login Flow (`login` method):**

1. Normalize email
2. Look up user by email (case-insensitive)
3. Verify password against stored BCrypt hash using `passwordEncoder.matches()`
4. Set authentication in Spring Security context
5. Check if user has a profile
6. Return AuthResponse with user details and profile status

**Logout Flow (`logout` method):**

1. Get the current HTTP session (if it exists)
2. Invalidate the session
3. Clear the SecurityContext

**Session Management (`setAuthenticationInContext`):**

```java
private void setAuthenticationInContext(User user, HttpServletRequest httpRequest) {
    UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(user.getEmail(), null, Collections.emptyList());
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    HttpSession session = httpRequest.getSession(true);
    session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
}
```

This method:
1. Creates a `UsernamePasswordAuthenticationToken` with the user's email as the principal
2. Stores it in the `SecurityContextHolder` (thread-local storage)
3. Gets or creates the HTTP session
4. Stores the SecurityContext in the session for persistence across requests

When `getCurrentUser()` is called on subsequent requests, Spring Security retrieves the SecurityContext from the session, and the user's email is available via `SecurityContextHolder.getContext().getAuthentication().getName()`.

### 11.2 UserService

```java
@Service
public class UserService {
    private final UserRepository userRepository;
    private final InterestRepository interestRepository;
    private final PasswordEncoder passwordEncoder;
```

**Key Responsibilities:**

- **`createUser(User)`** — Validates required fields, checks for duplicate emails, hashes the password, saves to database
- **`getAllUsers()`** — Returns all users (admin endpoint)
- **`getUserById(Long)`** — Finds a user or throws `UserNotFoundException`
- **`updateUser(Long, User)`** — Updates name and email, checks for email conflicts, preserves password
- **`deleteUser(Long)`** — Transactional: first deletes all related interests, then deletes the user

**User Deletion and Cascade:**

```java
@Transactional
public void deleteUser(Long userId) {
    User user = getUserById(userId);
    interestRepository.deleteBySenderUserIdOrReceiverUserId(userId, userId);
    userRepository.delete(user);
}
```

This method is `@Transactional` because it performs multiple database operations that must succeed or fail atomically. The interests are deleted first because they have foreign key constraints pointing to the users table. If we tried to delete the user first, MySQL would throw a foreign key constraint violation.

The `deleteBySenderUserIdOrReceiverUserId` method uses an `OR` condition to find all interests where the user is either the sender or the receiver.

### 11.3 ProfileService

```java
@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UserService userService;
```

**Profile Creation:**

```java
public Profile createProfile(Long userId, ProfileRequest request) {
    User user = userService.getUserById(userId);
    if (profileRepository.findByUserUserId(userId).isPresent()) {
        throw new IllegalArgumentException("This user already has a profile");
    }
    Profile profile = mapRequestToProfile(request, new Profile());
    profile.setUser(user);
    return profileRepository.save(profile);
}
```

1. Verify the user exists
2. Check that the user doesn't already have a profile (one-to-one constraint)
3. Map DTO fields to entity
4. Associate profile with user
5. Save to database

**Profile Search:**

```java
public List<Profile> searchProfiles(String gender, String city, Integer age) {
    return profileRepository.searchProfiles(emptyToNull(gender), emptyToNull(city), age);
}
```

Converts empty strings to null before passing to the repository, because the JPQL query checks for `NULL` to skip optional filters. An empty string `""` would not match `IS NULL` and would try to match profiles with an empty city, returning no results.

**Map Request to Profile:**

```java
private Profile mapRequestToProfile(ProfileRequest request, Profile profile) {
    if (request.getAge() == null || request.getAge() < 18) {
        throw new IllegalArgumentException("Age must be 18 or above");
    }
    if (request.getGender() == null || request.getGender().isBlank()) {
        throw new IllegalArgumentException("Gender is required");
    }
    if (request.getCity() == null || request.getCity().isBlank()) {
        throw new IllegalArgumentException("City is required");
    }
    profile.setAge(request.getAge());
    profile.setGender(request.getGender());
    profile.setCity(request.getCity());
    profile.setEducation(request.getEducation());
    profile.setOccupation(request.getOccupation());
    profile.setAbout(request.getAbout());
    return profile;
}
```

This private method serves dual purposes: it validates business rules and maps DTO fields to entity fields. By accepting an existing `Profile` instance, it can be used for both creation (with a new `Profile()`) and updates (with an existing profile loaded from the database).

### 11.4 InterestService

```java
@Service
public class InterestService {
    private static final String PENDING = "PENDING";
    private static final String ACCEPTED = "ACCEPTED";
    private static final String REJECTED = "REJECTED";
    private final InterestRepository interestRepository;
    private final UserService userService;
```

**Send Interest:**

```java
@Transactional
public Interest sendInterest(Long senderId, Long receiverId) {
    if (senderId.equals(receiverId)) {
        throw new IllegalArgumentException("A user cannot send interest to themselves");
    }
    if (interestRepository.existsBySenderUserIdAndReceiverUserIdAndStatus(senderId, receiverId, PENDING)) {
        throw new IllegalStateException("An interest request is already pending for this user");
    }
    User sender = userService.getUserById(senderId);
    User receiver = userService.getUserById(receiverId);
    Interest interest = new Interest();
    interest.setSender(sender);
    interest.setReceiver(receiver);
    interest.setStatus(PENDING);
    return interestRepository.save(interest);
}
```

**Business Rules:**

1. **Self-interest prevention:** A user cannot send an interest to themselves
2. **Duplicate prevention:** If a pending interest already exists from the same sender to the same receiver, a new one cannot be sent. This prevents spam.

Note the distinction between exception types:
- `IllegalArgumentException` for business rule violations (client error)
- `IllegalStateException` for state-related conflicts (the interest already exists in a specific state)

**Accept/Reject:**

```java
@Transactional
public Interest acceptInterest(Long interestId) {
    return updateStatus(interestId, ACCEPTED);
}

@Transactional
public Interest rejectInterest(Long interestId) {
    return updateStatus(interestId, REJECTED);
}
```

Both methods delegate to a shared `updateStatus` method, which loads the interest, updates its status, and saves it back.

---

## 12. Controller Layer

Controllers handle HTTP request routing, response formatting, and input validation. They are thin layers that delegate to services.

### 12.1 AuthController

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {
        return new ResponseEntity<>(authService.register(request, httpRequest), HttpStatus.CREATED);
    }
```

**Annotations:**

- **`@RestController`** — Combines `@Controller` and `@ResponseBody`. Every method return value is serialized to JSON and written to the HTTP response body.
- **`@RequestMapping("/api/auth")`** — All endpoints in this controller are prefixed with `/api/auth`.
- **`@PostMapping("/register")`** — Handles `POST /api/auth/register`.
- **`@Valid`** — Triggers Jakarta Bean Validation on the request body. If validation fails, Spring throws `MethodArgumentNotValidException`.
- **`@RequestBody`** — Deserializes the JSON request body into a `RegisterRequest` object.
- **`HttpServletRequest`** — Injected by Spring to access the HTTP session for authentication setup.

**HTTP Status Codes:**

| Endpoint | Status | Reason |
|---|---|---|
| `POST /register` | 201 Created | New resource created |
| `POST /login` | 200 OK | Authentication successful |
| `POST /logout` | 200 OK | Operation completed |
| `GET /me` | 200 OK | Current user returned |

### 12.2 UserController

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
```

Standard CRUD endpoints for User management. These are protected by authentication (the `/api/** → authenticated` rule in SecurityConfig).

**ResponseEntity Usage:**

```java
public ResponseEntity<User> createUser(@RequestBody User user) {
    return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
}
```

`ResponseEntity` gives full control over the HTTP response: body, status code, headers. `HttpStatus.CREATED` (201) is used for resource creation.

```java
public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long userId) {
    userService.deleteUser(userId);
    return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
}
```

For operations that don't return a resource (like deletion), a simple message map is returned with 200 OK.

### 12.3 ProfileController

```java
@RestController
@RequestMapping("/api/profiles")
public class ProfileController {
    private final ProfileService profileService;
```

**Profile Creation with Path Variable:**

```java
@PostMapping("/user/{userId}")
public ResponseEntity<Profile> createProfile(
        @PathVariable Long userId,
        @Valid @RequestBody ProfileRequest request) {
    return new ResponseEntity<>(profileService.createProfile(userId, request), HttpStatus.CREATED);
}
```

The profile is created for a specific user identified by `{userId}` in the URL path. The `@PathVariable` annotation extracts this value from the URL.

**Search Endpoint with Optional Parameters:**

```java
@GetMapping("/search")
public ResponseEntity<List<Profile>> searchProfiles(
        @RequestParam(required = false) String gender,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) Integer age) {
    return ResponseEntity.ok(profileService.searchProfiles(gender, city, age));
}
```

All three parameters are `required = false`, meaning they can be omitted entirely. This allows flexible searches like:
- `/api/profiles/search` — All profiles
- `/api/profiles/search?gender=Female` — All female profiles
- `/api/profiles/search?gender=Female&city=Mumbai` — Female profiles in Mumbai
- `/api/profiles/search?age=25` — All 25-year-old profiles

### 12.4 InterestController

```java
@RestController
@RequestMapping("/api/interests")
public class InterestController {
    private final InterestService interestService;
```

**Interest Lifecycle Endpoints:**

| Method | Path | Action |
|---|---|---|
| `POST` | `/send` | Send a new interest |
| `GET` | `/sent/{senderId}` | View sent interests |
| `GET` | `/received/{receiverId}` | View received interests |
| `PUT` | `/{interestId}/accept` | Accept an interest |
| `PUT` | `/{interestId}/reject` | Reject an interest |
| `DELETE` | `/{interestId}` | Delete an interest |

**RESTful Conventions:**

- `POST` for creating resources
- `GET` for reading resources
- `PUT` for updating resources (accepting/rejecting changes the status)
- `DELETE` for removing resources

```java
@DeleteMapping("/{interestId}")
public ResponseEntity<Void> deleteInterest(@PathVariable Long interestId) {
    interestService.deleteInterest(interestId);
    return ResponseEntity.noContent().build();
}
```

`ResponseEntity.noContent().build()` returns HTTP 204 No Content — appropriate for successful deletions where no response body is needed.

---

## 13. Exception Handling Architecture

The exception handling follows a centralized pattern using `@RestControllerAdvice` and `@ExceptionHandler`.

### Custom Exception Classes

The project defines four custom exception classes, all extending `RuntimeException`:

| Exception | HTTP Status | When Thrown |
|---|---|---|
| `UserNotFoundException` | 404 Not Found | User ID doesn't match any record |
| `ProfileNotFoundException` | 404 Not Found | Profile ID or user's profile doesn't exist |
| `InterestNotFoundException` | 404 Not Found | Interest ID doesn't match any record |
| `DuplicateEmailException` | 409 Conflict | Email already registered during registration |

### GlobalExceptionHandler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
```

**`@RestControllerAdvice`** combines `@ControllerAdvice` and `@ResponseBody`. It intercepts exceptions thrown by any `@RestController` method and converts them to JSON responses.

**Exception Handlers:**

| Handler | Catches | HTTP Status |
|---|---|---|
| `handleNotFound` | `UserNotFoundException`, `ProfileNotFoundException`, `InterestNotFoundException` | 404 |
| `handleConflict` | `DuplicateEmailException`, `IllegalStateException` | 409 |
| `handleBadCredentials` | `BadCredentialsException` | 401 |
| `handleValidationErrors` | `MethodArgumentNotValidException` | 400 |
| `handleIllegalArgument` | `IllegalArgumentException` | 400 |
| `handleGenericException` | `Exception` (catch-all) | 500 |

**Standardized Error Response:**

```java
private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now().toString());
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());
    body.put("message", message);
    return new ResponseEntity<>(body, status);
}
```

Every error response follows the same structure:

```json
{
    "timestamp": "2024-01-15T10:30:00",
    "status": 404,
    "error": "Not Found",
    "message": "User not found with ID: 42"
}
```

`LinkedHashMap` is used instead of `HashMap` to maintain insertion order, so the JSON output has a predictable field order.

**Validation Error Handling:**

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .reduce((a, b) -> a + "; " + b)
            .orElse("Validation failed");
    return buildResponse(HttpStatus.BAD_REQUEST, message);
}
```

When `@Valid` fails, Spring throws `MethodArgumentNotValidException`. This handler collects all field errors and joins them into a single semicolon-separated message:

```json
{
    "timestamp": "2024-01-15T10:30:00",
    "status": 400,
    "error": "Bad Request",
    "message": "name: Name is required; email: Invalid email format; password: Password must be at least 6 characters long"
}
```

---

## 14. Database Schema Design

The database `matrimonial_jpa_db` contains three tables:

### users Table

| Column | Type | Constraints | Description |
|---|---|---|---|
| `user_id` | BIGINT | PK, AUTO_INCREMENT | Unique user identifier |
| `name` | VARCHAR(255) | NOT NULL | User's display name |
| `email` | VARCHAR(255) | NOT NULL, UNIQUE | Email address (login credential) |
| `password` | VARCHAR(255) | NOT NULL | BCrypt-hashed password |

### profiles Table

| Column | Type | Constraints | Description |
|---|---|---|---|
| `profile_id` | BIGINT | PK, AUTO_INCREMENT | Unique profile identifier |
| `user_id` | BIGINT | FK → users.user_id, NOT NULL, UNIQUE | One-to-one link to user |
| `age` | INT | NOT NULL | User's age |
| `gender` | VARCHAR(255) | NOT NULL | "Male" or "Female" |
| `city` | VARCHAR(255) | NOT NULL | User's city |
| `education` | VARCHAR(255) | NULLABLE | Educational qualification |
| `occupation` | VARCHAR(255) | NULLABLE | Professional occupation |
| `about` | VARCHAR(1000) | NULLABLE | Self-description bio |

### interests Table

| Column | Type | Constraints | Description |
|---|---|---|---|
| `interest_id` | BIGINT | PK, AUTO_INCREMENT | Unique interest identifier |
| `sender_id` | BIGINT | FK → users.user_id, NOT NULL | User who sent the interest |
| `receiver_id` | BIGINT | FK → users.user_id, NOT NULL | User who received the interest |
| `status` | VARCHAR(255) | NOT NULL | "PENDING", "ACCEPTED", or "REJECTED" |

### Entity Relationship Diagram

```
┌─────────────┐       ┌─────────────────┐       ┌─────────────┐
│   users     │       │    profiles     │       │  interests  │
├─────────────┤       ├─────────────────┤       ├─────────────┤
│ user_id PK  │◄─1:1──│ profile_id PK   │       │interest_id PK│
│ name        │       │ user_id FK,UQ   │       │sender_id FK │──┐
│ email       │       │ age             │       │receiver_id FK│──┤
│ password    │       │ gender          │       │status       │  │
└─────────────┘       │ city            │       └─────────────┘  │
       ▲              │ education       │              │          │
       │              │ occupation      │              │          │
       └──────────────│ about           │              │          │
            1:1       └─────────────────┘              │          │
                                                       │          │
         interests.sender_id ──────────────────────────┘          │
         interests.receiver_id ───────────────────────────────────┘
                              (both reference users.user_id)
```

---

## 15. Seed Data (data.sql)

The `data.sql` file populates the database with test data on every application startup using `INSERT IGNORE` statements.

### Test Users (13 total)

The seed data creates 12 regular users and 1 admin user (ID 60):

| ID | Name | Email |
|---|---|---|
| 1 | Sriyaan | sriyaan@example.com |
| 2 | Priya Patel | priya@example.com |
| 3 | Rohan Mehta | rohan@example.com |
| 4 | Ananya Singh | ananya@example.com |
| 5 | Vikram Reddy | vikram@example.com |
| 6 | Meera Nair | meera@example.com |
| 7 | Arjun Kapoor | arjun@example.com |
| 8 | Diya Joshi | diya@example.com |
| 9 | Kabir Malhotra | kabir@example.com |
| 10 | Isha Agarwal | isha@example.com |
| 11 | Siddharth Rao | siddharth@example.com |
| 12 | Nisha Verma | nisha@example.com |
| 60 | Admin User | admin@example.com |

**All users share the same BCrypt password hash:** `$2b$10$TpVRCNsQ1jAhSlvHmueDh.qp9Kft0gYUJoezwuYT6qOmUXcZ1KJa.`

This is a pre-hashed BCrypt value (cost factor 10). The actual password is likely something simple for development (e.g., "password123" or "admin123"). The passwords are pre-hashed because `data.sql` runs raw SQL — it cannot call Spring's `PasswordEncoder` bean to hash at runtime.

### Test Profiles (13 total)

Each user has a corresponding profile with realistic matrimonial data:

| User ID | Age | Gender | City | Education | Occupation |
|---|---|---|---|---|---|
| 1 | 24 | Male | Delhi | B.Tech CSE | Software Developer |
| 2 | 25 | Female | Mumbai | MBA Finance | Financial Analyst |
| 3 | 30 | Male | Bangalore | M.Tech Electronics | Hardware Engineer |
| 4 | 27 | Female | Delhi | B.Com | Chartered Accountant |
| 5 | 29 | Male | Hyderabad | B.Tech IT | Product Manager |
| 6 | 26 | Female | Chennai | M.Sc Data Science | Data Analyst |
| 7 | 31 | Male | Jaipur | MBA Marketing | Marketing Head |
| 8 | 24 | Female | Kolkata | B.A English Literature | Content Writer |
| 9 | 23 | Male | Mumbai | B.Tech Mechanical | Automobile Engineer |
| 10 | 23 | Female | Pune | B.Sc Nursing | Nurse |
| 11 | 32 | Male | Delhi | LLB | Lawyer |
| 12 | 26 | Female | Bangalore | B.Des Fashion | Fashion Designer |
| 60 | 35 | Male | Mumbai | Ph.D System Admin | System Administrator |

The `about` fields contain varied personality descriptions that make the test data feel realistic for a matrimonial platform.

### Test Interests (6 total)

| ID | Sender | Receiver | Status |
|---|---|---|---|
| 1 | Sriyaan (1) | Priya Patel (2) | PENDING |
| 2 | Rohan Mehta (3) | Ananya Singh (4) | ACCEPTED |
| 3 | Vikram Reddy (5) | Meera Nair (6) | PENDING |
| 4 | Arjun Kapoor (7) | Priya Patel (2) | PENDING |
| 5 | Kabir Malhotra (9) | Isha Agarwal (10) | PENDING |
| 6 | Siddharth Rao (11) | Nisha Verma (12) | PENDING |

This creates a mix of pending and accepted interests for testing the interest management workflow.

### INSERT IGNORE

The `INSERT IGNORE` syntax is critical here. Since `data.sql` runs on every startup, the insert statements would fail on the second run due to duplicate primary keys. `INSERT IGNORE` silently skips rows that would cause duplicate key errors, making the seed data idempotent.

---

## 16. Complete API Reference

### Authentication Endpoints

| Method | Path | Auth Required | Request Body | Response | Status |
|---|---|---|---|---|---|
| `POST` | `/api/auth/register` | No | `RegisterRequest` | `AuthResponse` | 201 |
| `POST` | `/api/auth/login` | No | `LoginRequest` | `AuthResponse` | 200 |
| `POST` | `/api/auth/logout` | No | — | `{message}` | 200 |
| `GET` | `/api/auth/me` | Yes | — | `AuthResponse` | 200 |

### User Endpoints (All Require Auth)

| Method | Path | Request Body | Response | Status |
|---|---|---|---|---|
| `POST` | `/api/users` | `User` | `User` | 201 |
| `GET` | `/api/users` | — | `List<User>` | 200 |
| `GET` | `/api/users/{userId}` | — | `User` | 200 |
| `PUT` | `/api/users/{userId}` | `User` | `User` | 200 |
| `DELETE` | `/api/users/{userId}` | — | `{message}` | 200 |

### Profile Endpoints (All Require Auth)

| Method | Path | Request Body | Response | Status |
|---|---|---|---|---|
| `POST` | `/api/profiles/user/{userId}` | `ProfileRequest` | `Profile` | 201 |
| `GET` | `/api/profiles` | — | `List<Profile>` | 200 |
| `GET` | `/api/profiles/search?gender=&city=&age=` | — | `List<Profile>` | 200 |
| `GET` | `/api/profiles/user/{userId}` | — | `Profile` | 200 |
| `GET` | `/api/profiles/{profileId}` | — | `Profile` | 200 |
| `PUT` | `/api/profiles/{profileId}` | `ProfileRequest` | `Profile` | 200 |
| `DELETE` | `/api/profiles/{profileId}` | — | `{message}` | 200 |

### Interest Endpoints (All Require Auth)

| Method | Path | Request Body | Response | Status |
|---|---|---|---|---|
| `POST` | `/api/interests/send` | `SendInterestRequest` | `Interest` | 201 |
| `GET` | `/api/interests/sent/{senderId}` | — | `List<Interest>` | 200 |
| `GET` | `/api/interests/received/{receiverId}` | — | `List<Interest>` | 200 |
| `PUT` | `/api/interests/{interestId}/accept` | — | `Interest` | 200 |
| `PUT` | `/api/interests/{interestId}/reject` | — | `Interest` | 200 |
| `DELETE` | `/api/interests/{interestId}` | — | — | 204 |

### Error Response Format (All Endpoints)

```json
{
    "timestamp": "2024-01-15T10:30:00",
    "status": 400,
    "error": "Bad Request",
    "message": "description of the error"
}
```

---

## 17. Authentication Flow Deep Dive

### Registration Flow

```
Client                    Server                    Database
  │                         │                          │
  │  POST /api/auth/register│                          │
  │  {name, email, pass,   │                          │
  │   confirmPass}         │                          │
  │────────────────────────>│                          │
  │                         │  Check duplicate email   │
  │                         │─────────────────────────>│
  │                         │<─────────────────────────│
  │                         │                          │
  │                         │  BCrypt hash password    │
  │                         │  Save user               │
  │                         │─────────────────────────>│
  │                         │<─────────────────────────│
  │                         │                          │
  │                         │  Create session           │
  │                         │  Store SecurityContext   │
  │                         │                          │
  │  201 Created            │                          │
  │  {userId, name, email, │                          │
  │   hasProfile: false}    │                          │
  │  Set-Cookie: JSESSIONID │                          │
  │<────────────────────────│                          │
```

### Login Flow

```
Client                    Server                    Database
  │                         │                          │
  │  POST /api/auth/login   │                          │
  │  {email, password}      │                          │
  │────────────────────────>│                          │
  │                         │  Find user by email      │
  │                         │─────────────────────────>│
  │                         │<─────────────────────────│
  │                         │                          │
  │                         │  BCrypt.verify(password, │
  │                         │    storedHash)           │
  │                         │                          │
  │                         │  Create session           │
  │                         │  Store SecurityContext   │
  │                         │                          │
  │  200 OK                 │                          │
  │  {userId, name, email, │                          │
  │   hasProfile, profileId}│                          │
  │  Set-Cookie: JSESSIONID │                          │
  │<────────────────────────│                          │
```

### Authenticated Request Flow

```
Client                    Server                    Database
  │                         │                          │
  │  GET /api/profiles      │                          │
  │  Cookie: JSESSIONID=xxx │                          │
  │────────────────────────>│                          │
  │                         │  Extract session from    │
  │                         │  JSESSIONID cookie       │
  │                         │  Load SecurityContext    │
  │                         │  from session            │
  │                         │                          │
  │                         │  Check authentication    │
  │                         │  → Email from context    │
  │                         │                          │
  │                         │  Execute controller      │
  │                         │─────────────────────────>│
  │                         │<─────────────────────────│
  │  200 OK                 │                          │
  │  [...]                  │                          │
  │<────────────────────────│                          │
```

### Session Lifecycle

1. **Creation:** When `httpRequest.getSession(true)` is called in `setAuthenticationInContext`, Tomcat creates a new session and generates a `JSESSIONID` cookie
2. **Storage:** The `SecurityContext` (containing the user's authentication) is stored as a session attribute
3. **Persistence:** On subsequent requests, the browser sends the `JSESSIONID` cookie. Tomcat finds the existing session and loads the `SecurityContext`
4. **Invalidation:** When `session.invalidate()` is called during logout, the session is destroyed and the `JSESSIONID` cookie becomes invalid
5. **Timeout:** Sessions expire after 30 minutes of inactivity by default (Tomcat's `session-timeout`)

---

## 18. Design Patterns & Architectural Decisions

### Layered Architecture

The application follows a strict three-tier layered architecture:

```
┌──────────────────────────────────────────┐
│            Controller Layer              │
│  (HTTP routing, request/response mapping)│
├──────────────────────────────────────────┤
│            Service Layer                 │
│  (Business logic, transactions, rules)   │
├──────────────────────────────────────────┤
│          Repository Layer                │
│  (Database access, query execution)      │
├──────────────────────────────────────────┤
│            Database                      │
│  (MySQL tables: users, profiles,         │
│   interests)                             │
└──────────────────────────────────────────┘
```

**Dependency Flow:** Controllers depend on Services. Services depend on Repositories. Repositories depend on the JPA EntityManager. No circular dependencies exist.

### Dependency Injection (Constructor Injection)

Every service and controller uses **constructor injection**:

```java
private final AuthService authService;

public AuthController(AuthService authService) {
    this.authService = authService;
}
```

This is the Spring-recommended approach (over field injection with `@Autowired`) because:
1. Dependencies are immutable (`final` fields)
2. Dependencies are clearly visible in the constructor
3. The class is testable — you can pass mock dependencies in unit tests
4. Required dependencies fail at compile time (non-null constructor parameters)

### DTO Pattern

The application separates entities (database representation) from DTOs (API representation):

- **Entities** (User, Profile, Interest) map directly to database tables
- **DTOs** (LoginRequest, RegisterRequest, AuthResponse, etc.) define the API contract

Benefits:
1. **Security:** Passwords are never exposed in responses (`@JsonIgnore` + DTO)
2. **Decoupling:** Changing the database schema doesn't break the API contract
3. **Flexibility:** Different endpoints can return different shapes of data
4. **Validation:** DTOs carry validation annotations specific to input requirements

### Repository Pattern

Spring Data JPA's repository abstraction provides a clean interface between business logic and database access:

- No need to write SQL or JPQL for standard CRUD operations
- Method name derivation generates queries from method names
- Custom `@Query` annotations for complex queries
- `Optional` return types enforce null handling
- Built-in pagination support (not used here but available)

### Global Exception Handling

The `@RestControllerAdvice` pattern centralizes error handling:

- Controllers throw exceptions (never catch and return error responses)
- `GlobalExceptionHandler` catches exceptions and converts them to consistent JSON responses
- Each exception type maps to an appropriate HTTP status code
- Error responses follow a standardized format (timestamp, status, error, message)

---

## 19. CORS Configuration Explained

### Why CORS is Needed

The **Same-Origin Policy** is a fundamental browser security mechanism. It prevents a web page at `http://localhost:3000` from making AJAX requests to `http://localhost:8080` because they are different origins (different ports = different origins).

CORS (Cross-Origin Resource Sharing) is the standard mechanism to relax this restriction. The browser sends a **preflight request** (OPTIONS) before the actual request to ask the server which origins, methods, and headers are allowed.

### The Preflight Flow

```
Browser                        Server
  │                              │
  │  OPTIONS /api/auth/login     │  ← Preflight request
  │  Origin: http://localhost:3000
  │  Access-Control-Request-Method: POST
  │  Access-Control-Request-Headers: Content-Type
  │─────────────────────────────>│
  │                              │
  │  200 OK                      │  ← Preflight response
  │  Access-Control-Allow-Origin: http://localhost:3000
  │  Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
  │  Access-Control-Allow-Headers: *
  │  Access-Control-Allow-Credentials: true
  │  Access-Control-Max-Age: 3600
  │<─────────────────────────────│
  │                              │
  │  POST /api/auth/login        │  ← Actual request (with cookies)
  │  Origin: http://localhost:3000
  │  Cookie: JSESSIONID=xxx
  │─────────────────────────────>│
  │                              │
  │  200 OK                      │  ← Actual response
  │<─────────────────────────────│
```

The `Access-Control-Max-Age: 3600` tells the browser to cache the preflight result for 1 hour, reducing unnecessary preflight requests.

### `allowCredentials: true`

This is essential for session-based authentication. When `allowCredentials` is true:
- The browser includes cookies (JSESSIONID) in cross-origin requests
- The server can set cookies in cross-origin responses
- The `Access-Control-Allow-Origin` header cannot be `*` — it must specify the exact origin

---

## 20. Database Relationships & Cascade Behavior

### OneToOne: User ↔ Profile

**Owning Side:** Profile (has the `@JoinColumn`)

```
users table                    profiles table
┌──────────┐                  ┌────────────┐
│ user_id  │◄─────────────────│ user_id FK │
└──────────┘    One-to-One    └────────────┘
```

The `profiles.user_id` column has a `UNIQUE` constraint, ensuring one-to-one cardinality. Without `UNIQUE`, multiple profiles could reference the same user, making it a one-to-many relationship.

**Cascade Configuration:**

```java
@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
```

`CascadeType.ALL` means all JPA lifecycle operations cascade from User to Profile:

| Cascade Operation | Effect |
|---|---|
| `PERSIST` | Saving a User also saves its Profile |
| `MERGE` | Updating a User also updates its Profile |
| `REMOVE` | Deleting a User also deletes its Profile |
| `REFRESH` | Refreshing a User also refreshes its Profile |
| `DETACH` | Detaching a User also detaches its Profile |

**Important caveat:** The cascade from User→Profile is defined on the User side (`mappedBy`), but the `UserService.deleteUser()` method manually deletes interests before deleting the user because `CascadeType.ALL` on the `@OneToOne` relationship does not cascade to the `Interest` entity (which is a separate `@ManyToOne` relationship from Interest to User).

### ManyToOne: Interest → User (Sender)

```java
@ManyToOne
@JoinColumn(name = "sender_id", nullable = false)
private User sender;
```

Multiple Interest records can have the same sender. Each Interest has exactly one sender. The `sender_id` foreign key in the `interests` table references `users.user_id`.

### ManyToOne: Interest → User (Receiver)

```java
@ManyToOne
@JoinColumn(name = "receiver_id", nullable = false)
private User receiver;
```

Same as sender — multiple interests can be received by the same user.

### Why No Cascade on Interest Relationships

The Interest entity intentionally does NOT have cascade operations. This is a deliberate design choice:

- When a User is deleted, interests should be deleted via explicit `interestRepository.deleteBySenderUserIdOrReceiverUserId()` in `UserService.deleteUser()`
- Accepting/rejecting an interest should NOT cascade to the User entities
- Deleting an interest should NOT delete the sender or receiver

If cascade were enabled on the `@ManyToOne` relationships, deleting an Interest could accidentally delete the associated Users — catastrophic data loss.

---

## 21. Bean Validation Deep Dive

The application uses **Jakarta Bean Validation 3.0** (the successor to Java Validation / JSR 380), which is part of the `spring-boot-starter-validation` dependency.

### Validation Annotations Used

| Annotation | Applies To | Constraint |
|---|---|---|
| `@NotBlank` | String fields | Not null, not empty, not whitespace-only |
| `@Email` | String fields | Must match email pattern |
| `@NotNull` | Any field | Not null (allows empty strings) |
| `@Min(18)` | Numeric fields | Value must be ≥ 18 |
| `@Max(99)` | Numeric fields | Value must be ≤ 99 |
| `@Size(min=6)` | String fields | Length must be ≥ 6 characters |
| `@Size(max=100)` | String fields | Length must be ≤ 100 characters |
| `@Size(max=200)` | String fields | Length must be ≤ 200 characters |
| `@Size(max=1000)` | String fields | Length must be ≤ 1000 characters |

### How Validation Works in Spring Boot

1. A request arrives at a controller method with `@Valid @RequestBody LoginRequest request`
2. Spring MVC deserializes the JSON body into a `LoginRequest` instance
3. The `@Valid` annotation triggers `Validator` to check all Jakarta validation annotations on `LoginRequest`
4. If any constraint is violated, a `MethodArgumentNotValidException` is thrown
5. The `GlobalExceptionHandler.handleValidationErrors()` catches it
6. All field errors are collected and returned as a 400 Bad Request response

### Validation vs Business Logic

The project splits validation into two categories:

**Structural validation (annotations):** Handled by Jakarta Bean Validation before the controller method even executes. Examples: required fields, email format, min/max values.

**Business logic validation (programmatic):** Handled inside service methods. Examples: passwords must match (`RegisterRequest.isPasswordMatching()`), user must not have an existing profile (`ProfileService.createProfile()`), user cannot send interest to themselves (`InterestService.sendInterest()`).

This separation keeps controllers clean and moves business rule enforcement to the service layer where it belongs.

---

## 22. Transaction Management

### The `@Transactional` Annotation

Spring provides declarative transaction management through the `@Transactional` annotation:

```java
@Transactional
public void deleteUser(Long userId) {
    User user = getUserById(userId);
    interestRepository.deleteBySenderUserIdOrReceiverUserId(userId, userId);
    userRepository.delete(user);
}
```

**What `@Transactional` does:**

1. **Before the method executes:** Spring opens a database transaction (BEGIN)
2. **Method executes:** All database operations within the method use the same connection
3. **If the method completes successfully:** Spring commits the transaction (COMMIT)
4. **If any exception is thrown:** Spring rolls back the transaction (ROLLBACK)

**Why `deleteUser` needs `@Transactional`:**

The method performs two database operations:
1. Delete all interests involving this user
2. Delete the user itself

Without `@Transactional`, if step 1 succeeds but step 2 fails, the interests are deleted but the user remains — an inconsistent state. With `@Transactional`, both operations are atomic: either both succeed or both are rolled back.

**Why `sendInterest` needs `@Transactional`:**

The `sendInterest` method checks for duplicates and then saves. While the duplicate check and save are conceptually a single operation, wrapping them in a transaction prevents race conditions where two concurrent requests might both pass the duplicate check and both insert.

### Default Transaction Behavior

Spring's default rollback behavior for `@Transactional`:
- Rollback on **unchecked exceptions** (`RuntimeException` and its subclasses)
- Commit on **checked exceptions** (`Exception` and its subclasses)

Since all custom exceptions (`UserNotFoundException`, `DuplicateEmailException`, etc.) extend `RuntimeException`, they all trigger automatic rollback.

### Transaction Isolation

The default isolation level is `READ_COMMITTED` (MySQL's default), which means:
- A transaction cannot read uncommitted data from other transactions
- Non-repeatable reads are possible (reading the same row twice in one transaction might return different values if another transaction committed in between)

For this application's scale, the default isolation level is sufficient.

---

## 23. Entities vs DTOs — Mass Assignment Protection

### The Problem

If controller endpoints accepted entities directly as request bodies, a malicious client could send:

```json
{
    "name": "Hacker",
    "email": "hacker@evil.com",
    "password": "hacked",
    "userId": 999
}
```

The `userId` field could overwrite an existing user's ID, causing data corruption. This is called a **mass assignment vulnerability**.

### The Solution: DTOs

The project uses DTOs for all input data:

```java
public class ProfileRequest {
    private Integer age;
    private String gender;
    private String city;
    private String education;
    private String occupation;
    private String about;
}
```

The `ProfileRequest` only contains fields the client is allowed to set. There is no `userId`, `profileId`, or `user` field. The service layer explicitly sets the relationship:

```java
profile.setUser(user); // Set by service, not client
```

### JSON Deserialization Safety

The `@JsonIgnore` annotation on sensitive fields provides defense-in-depth:

```java
@JsonIgnore
@Column(nullable = false)
private String password;
```

Even if an endpoint returned a full `User` entity (which `getCurrentUser()` does through `AuthResponse`), the password is never included in the JSON output.

The `@JsonIgnoreProperties(ignoreUnknown = true)` on `Profile` prevents deserialization errors when the client sends unexpected fields:

```json
{
    "age": 25,
    "gender": "Female",
    "city": "Mumbai",
    "unexpectedField": "value"  // Silently ignored
}
```

---

## 24. JPQL Custom Queries vs Method-Name Queries

Spring Data JPA supports two approaches for defining queries:

### Method-Name Query Derivation

```java
Optional<User> findByEmail(String email);
boolean existsByEmail(String email);
List<Interest> findBySenderUserId(Long senderId);
```

**Advantages:**
- Type-safe: method parameters are typed
- No string-based query writing
- IDE support for refactoring
- Spring generates the query at startup

**Limitations:**
- Limited to simple conditions (equality, comparisons, pattern matching)
- Complex joins, aggregations, and subqueries are difficult or impossible
- Very long method names become unwieldy

**Supported Keywords:**

| Keyword | Example | JPQL Equivalent |
|---|---|---|
| `findBy` | `findByEmail` | `WHERE u.email = ?1` |
| `existsBy` | `existsByEmail` | `SELECT COUNT(*) > 0 WHERE email = ?1` |
| `deleteBy` | `deleteBySenderUserId` | `DELETE WHERE sender_id = ?1` |
| `IgnoreCase` | `findByEmailIgnoreCase` | `WHERE LOWER(email) = LOWER(?1)` |

### Custom JPQL Queries

```java
@Query("SELECT p FROM Profile p JOIN p.user u WHERE " +
       "(:gender IS NULL OR :gender = '' OR LOWER(p.gender) = LOWER(:gender)) AND " +
       "(:city IS NULL OR :city = '' OR LOWER(p.city) = LOWER(:city)) AND " +
       "(:age IS NULL OR p.age = :age)")
List<Profile> searchProfiles(@Param("gender") String gender,
                             @Param("city") String city,
                             @Param("age") Integer age);
```

**Advantages:**
- Full JPQL power: joins, subqueries, aggregations, CASE expressions
- Dynamic queries with optional parameters
- Named parameters (`:gender`, `:city`) for readability
- Complex conditional logic

**When to use which:**

| Scenario | Approach |
|---|---|
| Find entity by single field | Method-name query |
| Check existence by field | Method-name query |
| Delete by field condition | Method-name query |
| Dynamic multi-field search | Custom `@Query` |
| Join queries across entities | Custom `@Query` |
| Aggregations and grouping | Custom `@Query` |

The project uses method-name queries for simple single-entity operations and custom JPQL for the profile search feature that requires dynamic filtering with optional parameters.

---

## 25. Running & Testing the Application

### Prerequisites

1. **Java 21** — Required by the Maven compiler configuration
2. **MySQL 8.x** — Running on `localhost:3306` with root/root credentials
3. **Maven 3.8+** — For building and running the project

### Database Setup

The application automatically creates the `matrimonial_jpa_db` database on first run (via `createDatabaseIfNotExist=true`). Hibernate creates the tables (`ddl-auto=update`), and `data.sql` seeds the test data.

### Starting the Application

```bash
# Navigate to the backend directory
cd backend/

# Run with Maven
mvn spring-boot:run

# Or build and run the JAR
mvn clean package
java -jar target/matrimonial-jpa-backend-1.0.0.jar
```

The application starts at `http://localhost:8080`.

### Swagger UI

Navigate to `http://localhost:8080/swagger-ui.html` to access the interactive API documentation. From there, you can:

1. See all available endpoints organized by controller
2. View request/response schemas
3. Execute API calls with "Try It Out"
4. Inspect validation constraints on request bodies

### Testing the Authentication Flow

```bash
# 1. Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","password":"password123","confirmPassword":"password123"}' \
  -c cookies.txt

# 2. The response sets a JSESSIONID cookie in cookies.txt
# 3. Use the cookie for authenticated requests
curl http://localhost:8080/api/auth/me -b cookies.txt

# 4. Login with the registered user
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}' \
  -c cookies.txt

# 5. Search for profiles
curl "http://localhost:8080/api/profiles/search?gender=Female&city=Mumbai" -b cookies.txt

# 6. Logout
curl -X POST http://localhost:8080/api/auth/logout -b cookies.txt
```

### Frontend Integration

The Next.js frontend at `localhost:3000` communicates with this backend. The CORS configuration allows requests from both `localhost:3000` and `localhost:3001`. The frontend should:

1. Store the session cookie (`JSESSIONID`) automatically via browser cookie handling
2. Send `credentials: 'include'` with fetch requests to include cookies cross-origin
3. Call `GET /api/auth/me` on page load to check if the user is still authenticated
4. Handle the `hasProfile` flag in `AuthResponse` to show the appropriate view

### Production Considerations

This backend is designed for development and demonstration. For production deployment:

1. **Externalize configuration** — Move database credentials to environment variables
2. **Enable CSRF protection** — Add CSRF tokens for form-based operations
3. **Use Redis/JDBC sessions** — Replace in-memory sessions for horizontal scaling
4. **Change `ddl-auto`** — Set to `none` and use Flyway/Liquibase for schema management
5. **Add rate limiting** — Prevent brute-force login attempts
6. **Enable HTTPS** — Encrypt all traffic in transit
7. **Use a connection pool** — Configure HikariCP for production-grade connection management
8. **Add input sanitization** — Prevent XSS in the `about` and `name` fields
9. **Implement role-based access** — Add admin roles for user management endpoints
10. **Add logging** — Configure structured logging with Logback for production monitoring

---

## Appendix A: Package Structure Summary

```
com.matrimonial/
├── MatrimonialApplication.java          # Application entry point
├── config/
│   ├── SecurityConfig.java              # Spring Security configuration
│   └── OpenApiConfig.java               # Swagger/OpenAPI configuration
├── controller/
│   ├── AuthController.java              # /api/auth endpoints
│   ├── UserController.java              # /api/users endpoints
│   ├── ProfileController.java           # /api/profiles endpoints
│   └── InterestController.java          # /api/interests endpoints
├── dto/
│   ├── AuthResponse.java                # Login/register response
│   ├── LoginRequest.java                # Login request body
│   ├── RegisterRequest.java             # Register request body
│   ├── SendInterestRequest.java         # Send interest request body
│   └── ProfileRequest.java              # Create/update profile body
├── entity/
│   ├── User.java                        # users table entity
│   ├── Profile.java                     # profiles table entity
│   └── Interest.java                    # interests table entity
├── exception/
│   ├── GlobalExceptionHandler.java      # Centralized exception handling
│   ├── UserNotFoundException.java       # 404 — user not found
│   ├── ProfileNotFoundException.java    # 404 — profile not found
│   ├── InterestNotFoundException.java   # 404 — interest not found
│   └── DuplicateEmailException.java     # 409 — email already exists
├── repository/
│   ├── UserRepository.java              # User CRUD + email lookups
│   ├── ProfileRepository.java           # Profile CRUD + search queries
│   └── InterestRepository.java          # Interest CRUD + relationship queries
└── service/
    ├── AuthService.java                 # Registration, login, logout, session
    ├── UserService.java                 # User CRUD + deletion with cascade
    ├── ProfileService.java              # Profile CRUD + search
    └── InterestService.java             # Interest send/accept/reject/delete
```

## Appendix B: Technology Version Matrix

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 LTS | Programming language |
| Spring Boot | 3.2.0 | Application framework |
| Spring Security | 6.2.0 (bundled) | Authentication & authorization |
| Spring Data JPA | 3.2.0 (bundled) | Data access layer |
| Hibernate | 6.4.0 (bundled) | JPA implementation / ORM |
| MySQL Connector/J | 8.x (bundled) | JDBC driver |
| SpringDoc OpenAPI | 2.3.0 | API documentation |
| Jakarta Bean Validation | 3.0 (bundled) | Input validation |
| BCrypt | Spring Security built-in | Password hashing |
| Tomcat | 10.1.x (bundled) | Embedded web server |
| Maven | 3.8+ | Build tool |

---

*This documentation covers every file, annotation, method, configuration property, and architectural decision in the HeartMate matrimonial backend. For questions or modifications, refer to the corresponding source file in the `backend/` directory.*
