# InternTrack — System Design Document

**Project:** InternTrack — Student Internship Application Management System
**Folder:** `BSC_InternTrack– By Vivek and Aditya`
**Contributors:** Vivek and Aditya
**Document version:** 1.0
**Status:** Implemented and running

---

## 1. Introduction

### 1.1 Purpose

InternTrack is a centralised web application that lets students manage the complete
lifecycle of their internship search in one place: profile, skill set, target
companies, internship opportunities, submitted applications, and the status of each
application. It also gives recruiters/companies a way to represent themselves and
publish internship openings, and it pushes an in-app notification to a student
whenever the status of one of their applications changes.

### 1.2 Scope

| In scope | Out of scope (see §14) |
|---|---|
| Student / Company / Skill profile CRUD | Resume file upload and parsing |
| Internship opportunity CRUD | Email / SMS delivery of notifications |
| Application submission and status tracking | Payment or stipend disbursement |
| In-app notification centre | External job-board scraping |
| REST API with OpenAPI (Swagger) documentation | Role-based authorisation at HTTP layer |
| Single-page-per-entity responsive web UI | Microservice decomposition |

### 1.3 Definitions

| Term | Meaning |
|---|---|
| Student | A person seeking an internship. Primary actor of the system. |
| Company | An employer that publishes internship openings. |
| Internship | An opening published by a company (title, stipend, deadline, required skills). |
| Application | A student's submission for one specific internship, with a tracked status. |
| Skill | A reusable tag (Java, React, MySQL …) categorised by domain. |
| User | The login identity (`STUDENT` or `COMPANY` role) linked to a Student or Company record via `refId`. |

---

## 2. Requirements

### 2.1 Functional requirements

| ID | Requirement | Implemented by |
|---|---|---|
| FR-01 | Register a user with a role and a reference to a student/company record | `AuthController` → `AuthService` |
| FR-02 | Log in with email and password | `AuthController` → `AuthService` |
| FR-03 | Create, view, update, delete student profiles | `StudentController` → `StudentService` |
| FR-04 | Create, view, update, delete companies | `CompanyController` → `CompanyService` |
| FR-05 | Create, view, update, delete internships, filtered by company | `InternshipController` → `InternshipService` |
| FR-06 | Create, view, update, delete skills | `SkillController` → `SkillService` |
| FR-07 | Attach skills to a student and to an internship; query by either side | `StudentSkillController`, `InternshipSkillController` |
| FR-08 | Submit an application for an internship, one per student per internship | `ApplicationController` → `ApplicationService` |
| FR-09 | Update an application through a controlled status lifecycle | `ApplicationServiceImpl.updateApplicationStatus` |
| FR-10 | Raise a notification for a student on every status change | `ApplicationServiceImpl.createNotificationForStudent` |
| FR-11 | List, mark read, and delete notifications for a user | `NotificationController` → `NotificationService` |
| FR-12 | Show aggregate counts (total and per status) on the dashboard | `GET /api/applications/stats/**` |
| FR-13 | Return consistent JSON errors for every failure mode | `GlobalExceptionHandler` |

### 2.2 Non-functional requirements

| ID | Requirement | How it is met |
|---|---|---|
| NFR-01 | Layered separation of concerns | Controller → Service → DAO → MySQL, one responsibility per package |
| NFR-02 | Single, uniform error contract | `@ControllerAdvice` maps exceptions to `{status, message, timestamp}` |
| NFR-03 | API discoverability | springdoc-openapi annotations on every endpoint, Swagger UI served by the app |
| NFR-04 | Data integrity | Foreign keys with `ON DELETE CASCADE`, `UNIQUE` constraints, service-level existence checks |
| NFR-05 | Password confidentiality | BCrypt hashing (cost factor 10); plaintext never stored or returned |
| NFR-06 | Simple deployment | Single Spring Boot fat JAR; no external message broker or cache required |
| NFR-07 | Responsive UI usable at 768 px and above | Single `style.css` with one breakpoint |
| NFR-08 | Reproducible database | `schema.sql` + `data.sql` executed on every start via `spring.sql.init` |

---

## 3. Technology Stack

### 3.1 Backend

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Language, LTS |
| Spring Boot | 3.2.5 | Application framework, auto-configuration, embedded Tomcat |
| Spring Web | 3.2.5 | REST controllers, JSON binding, CORS |
| Spring JDBC (`JdbcTemplate`) | 6.1.x | Explicit SQL access — no ORM, full control over queries |
| Spring Validation (Jakarta) | 3.0 | Bean-validation on request bodies |
| Spring Security Crypto | 6.2.x | `BCryptPasswordEncoder` only (no filter chain) |
| springdoc-openapi | 2.3.0 | Swagger UI at `/swagger-ui.html`, OpenAPI JSON at `/api-docs` |
| MySQL Connector/J | managed by Boot | JDBC driver |
| Maven | 3.8+ | Build, dependency management, `spring-boot:run` |
| MySQL | 8.x | System of record |

### 3.2 Frontend

| Technology | Purpose |
|---|---|
| HTML5 | Nine static pages served directly from `src/main/resources/static` |
| CSS3 | One hand-written stylesheet, CSS custom properties for theming, no framework |
| Vanilla JavaScript (ES6+) | One module per page plus a shared `api.js` fetch wrapper |
| `localStorage` | Single key `interntrack_user` holding the logged-in user object |

### 3.3 Why this stack

- **JDBC over JPA.** The domain is a straightforward normalised relational model with
  hand-written joins and counts. Explicit SQL keeps the DAO layer readable and avoids
  lazy-loading surprises, at the cost of hand-maintained `RowMapper`s.
- **Server-rendered static assets, not a SPA framework.** The UI is CRUD tables and
  modals. Serving plain files from the same origin as the API removes the need for a
  second host, a build step, and CORS in the normal path.
- **No message broker.** Notifications are written synchronously in the same request
  as the status change, which is sufficient at this scale.

---

## 4. Architectural Overview

### 4.1 Style

A **layered monolithic architecture**. One deployable unit, three internal layers, a
strict one-way dependency rule: a layer may only call the layer immediately below it.

```
┌──────────────────────────────────────────────────────────────┐
│  PRESENTATION                                                 │
│  static/*.html + static/js/*.js   │  controller/*.java       │
│  (browser)                        │  (@RestController)       │
└───────────────┬──────────────────┴──────────────┬────────────┘
                │  HTTP (JSON over /api/**)        │  direct call
                │                                  │
                │        ┌─────────────────────────▼───────────┐
                │        │  BUSINESS LOGIC                     │
                │        │  service/*Service + *ServiceImpl    │
                │        │  validation, rules, side-effects   │
                │        └─────────────────────────┬───────────┘
                │                                  │
                │        ┌─────────────────────────▼───────────┐
                │        │  DATA ACCESS                        │
                │        │  dao/*Dao (interface)                │
                │        │  dao/*DaoImpl (@Repository,         │
                │        │       JdbcTemplate + RowMapper)     │
                │        └─────────────────────────┬───────────┘
                │                                  │  JDBC
┌───────────────▼──────────────────────────────────▼───────────┐
│  MySQL — 9 tables, VARCHAR(36) PKs, FK ON DELETE CASCADE      │
└───────────────────────────────────────────────────────────────┘

  CROSS-CUTTING: exception/NotFoundException, DuplicateResourceException,
                 exception/GlobalExceptionHandler (@ControllerAdvice)
  CONFIG:       config/WebConfig (CORS), application.properties
```

### 4.2 Package map

| Package | Responsibility | Depends on |
|---|---|---|
| `com.internships` | `@SpringBootApplication` entry point | — |
| `com.internships.config` | CORS `WebMvcConfigurer` | — |
| `com.internships.controller` | HTTP contract, status codes, Swagger tags | `service` |
| `com.internships.service` | Business rules, validation, orchestration | `dao`, other services |
| `com.internships.dao` | SQL and row mapping | `model`, `JdbcTemplate` |
| `com.internships.model` | Plain POJOs (no annotations) | — |
| `com.internships.exception` | Custom exceptions + global handler | — |

### 4.3 Request lifecycle (worked example: status update)

```
PUT /api/applications/app-002/status?status=Selected
 │
 ├─ ApplicationController.updateApplicationStatus
 ├─ ApplicationServiceImpl.updateApplicationStatus
 │    1. load application              → ApplicationDao.findById
 │       (null ⇒ NotFoundException ⇒ 404)
 │    3. validate status ∈ ALLOWED_STATUSES  (else IllegalArgumentException ⇒ 400)
 │    4. ApplicationDao.updateStatus   → UPDATE applications SET status=? WHERE application_id=?
 │    5. re-read row                   → returns fresh entity
 │    6. if status changed: resolve the student's User
 │           UserDao.findByEmail(studentId + "@example.com")  → fallback UserDao.findById
 │    7. build Notification(id = "notif-" + UUID, isRead = false, type = STATUS_UPDATE)
 │    8. NotificationService.createNotification → NotificationDao.save
 │         (all notification failures are caught + logged, never fail the request)
 └─ 200 OK + updated application JSON
```

### 4.4 Deployment view

```
        Browser
           │  HTTP :8080
           ▼
  ┌──────────────────────────┐
  │ Spring Boot (Tomcat 8080)│  ├── /api/**        → controllers
  │  interntrack.jar         │  ├── /swagger-ui    → springdoc
  │                          │  └── /*.html, /css, /js → static resources
  └────────────┬─────────────┘
               │ JDBC :3307
               ▼
        ┌─────────────┐
        │   MySQL DB  │  database `interntrack`
        └─────────────┘
```

One process, one port, one database. No reverse proxy, no container orchestrator, no
external dependency.

---

## 5. Module Design

### 5.1 Module inventory

| Module | Controllers | Services | DAOs | Tables |
|---|---|---|---|---|
| Authentication | `AuthController` | `AuthService`, `UserService(Impl)` | `UserDao(Impl)` | `users` |
| Students | `StudentController` | `StudentService(Impl)` | `StudentDao(Impl)` | `students` |
| Companies | `CompanyController` | `CompanyService(Impl)` | `CompanyDao(Impl)` | `companies` |
| Internships | `InternshipController` | `InternshipService(Impl)` | `InternshipDao(Impl)` | `internships` |
| Skills | `SkillController` | `SkillService(Impl)` | `SkillDao(Impl)` | `skills` |
| Skill mapping (students) | `StudentSkillController` | `StudentSkillService(Impl)` | `StudentSkillDao(Impl)` | `student_skills` |
| Skill mapping (internships) | `InternshipSkillController` | `InternshipSkillService(Impl)` | `InternshipSkillDao(Impl)` | `internship_skills` |
| Applications | `ApplicationController` | `ApplicationService(Impl)` | `ApplicationDao(Impl)` | `applications` |
| Notifications | `NotificationController` | `NotificationService` | `NotificationDao(Impl)` | `notifications` |
| Cross-cutting | — | — | — | `GlobalExceptionHandler`, `WebConfig` |

### 5.2 Design rules applied everywhere

1. **Controllers are thin.** They bind, delegate, and wrap in `ResponseEntity`.
   No business logic, no SQL, no `try/catch`.
2. **Services own the rules.** Required-field checks, existence checks, duplicate
   checks, and status whitelisting all live here.
3. **DAOs are literal.** One `RowMapper` per entity, one method per statement, no
   branching on business conditions.
4. **Single-object lookups return `null`,** never throw. Translating `null` into a
   `NotFoundException` is the service layer's job — this keeps the DAO free of
   HTTP semantics.
5. **Existence is checked before insert/update,** so the API returns 404 with a clear
   message instead of leaking a foreign-key violation as a 500.
6. **IDs are `VARCHAR(36)` strings,** not auto-increment integers, so records can be
   seeded with readable ids (`student-001`, `intern-004`) and referenced across
   environments.

### 5.3 Key business rules

| Rule | Location | Behaviour on violation |
|---|---|---|
| Allowed application statuses | `ApplicationServiceImpl.ALLOWED_STATUSES` | `IllegalArgumentException` → **400** |
| Default status on create | `ApplicationServiceImpl.createApplication` | Blank status becomes `"Applied"` |
| Student email uniqueness | `StudentServiceImpl` | `DuplicateResourceException` → **409** |
| User email uniqueness | `AuthService.register`, `UserServiceImpl.createUser` | `DuplicateResourceException` → **409** |
| Skill name uniqueness | `SkillServiceImpl.createSkill` | `DuplicateResourceException` → **409** |
| Student may apply once per internship | DB `UNIQUE KEY unique_application (student_id, internship_id)` | Integrity violation → **500** via catch-all |
| Company must exist for an internship | `InternshipServiceImpl` | `NotFoundException` → **404** |
| Student and internship must exist for an application | `ApplicationServiceImpl` | `NotFoundException` → **404** |
| Skill mapping is idempotent | `StudentSkillServiceImpl`, `InternshipSkillServiceImpl` | Existing pair is skipped, no error |
| Notification on status change | `ApplicationServiceImpl.updateApplicationStatus` | Failures logged, never block the update |

### 5.4 Identifier strategy

| Entity | Format | Generated by |
|---|---|---|
| User | `user-` + first 8 chars of a UUID | **Server** — `AuthService.register` |
| Notification | `notif-` + full UUID | **Server** — `ApplicationServiceImpl` |
| Student | `student-` + `Date.now()` | Client (browser form) |
| Company | `company-` + `Date.now()` | Client (browser form) |
| Internship | `intern-` + `Date.now()` | Client (browser form) |
| Application | `app-` + `Date.now()` | Client (browser form) |
| Skill | caller-supplied | Client |
| StudentSkill / InternshipSkill | composite `(parentId, skillId)` | Not applicable — junction tables have no surrogate id |

> Design note: `skillId` and the entity ids above are currently supplied by the
> browser. The services validate non-blank and reject duplicates, so the schema stays
> consistent, but a hardened version would generate all identifiers server-side.

---

## 6. Data Design

See `DATABASE_DESIGN.md` for the full DDL, column-level detail and the ER diagram.
Summary of the model:

```
users ──────────────┐
                    │ 1
                    │
students ──< student_skills >── skills ──< internship_skills >── internships
   │                                                                   ▲
   │ 1                                                                 │ N
   │ N                                                                 │
   └──────────────────< applications >───────────────────────────────────┘
                              │
                              │ triggers
                              ▼
                        notifications
```

Nine tables, all with `TIMESTAMP DEFAULT CURRENT_TIMESTAMP`; `applications` also
carries `updated_at TIMESTAMP ... ON UPDATE CURRENT_TIMESTAMP`. Nine foreign keys, all
`ON DELETE CASCADE`, so removing a company removes its internships, and removing a
student removes their applications, skill links, and (through the user link)
notifications.

---

## 7. API Design

Full endpoint tables are in `API_REFERENCE.md`. Conventions:

- Base path `/api`; JSON in and out; UTF-8.
- `201 Created` on successful create, `204 No Content` on delete, `200 OK` otherwise.
- Resource nouns are plural; sub-resources use path segments
  (`/api/applications/student/{studentId}`).
- A status transition is a dedicated sub-resource action: `PUT /api/applications/{id}/status?status=...`.
- Aggregates are namespaced under `/stats` so they never collide with `/{id}`.
- Every controller method carries `@Tag` and `@Operation` for the Swagger UI.

Error contract for every non-2xx response:

```json
{ "status": 404, "message": "Application not found with id: app-999", "timestamp": "2026-09-30T16:20:11.482" }
```

| Status | Raised by | Typical message |
|---|---|---|
| 400 | `IllegalArgumentException` | `Invalid status: Pending. Allowed: [Applied, Shortlisted, Interview, Selected, Rejected]` |
| 404 | `NotFoundException` | `Student not found with id: student-999` |
| 409 | `DuplicateResourceException` | `A student with email bob@example.com already exists` |
| 500 | any other `Exception` | `An unexpected error occurred: ...` |

---

## 8. Security Design

### 8.1 What is implemented

| Control | Implementation |
|---|---|
| Password storage | `BCryptPasswordEncoder` (cost 10) in `AuthService` and `UserServiceImpl` |
| Credential comparison | `passwordEncoder.matches(raw, hash)` — constant-time by construction |
| Duplicate accounts | Unique check on `users.email` before insert, plus a DB `UNIQUE` constraint |
| Login enumeration | Unknown email and wrong password both raise the identical message `Invalid email or password` |
| Parameterised SQL | All DAO statements use `?` bind parameters via `JdbcTemplate` — no string concatenation, so no SQL injection surface |
| CORS allow-list | Only `http://localhost:8080` and `http://127.0.0.1:8080`, methods `GET/POST/PUT/DELETE/OPTIONS` |

### 8.2 What is deliberately not implemented in this version

These are recorded honestly as the security gap of the current build, not as design
decisions to keep:

- **No Spring Security filter chain.** `spring-security-crypto` is on the classpath
  for BCrypt only. There is no `SecurityFilterChain` bean, so every `/api/**` endpoint
  is reachable without credentials.
- **No token issuance.** `POST /api/auth/login` returns identity fields only — no JWT,
  no session cookie. `api.js` sends no `Authorization` header.
- **No server-side authorisation.** Role is stored on the user record but never
  checked; there is no `refId`-based ownership check, so any caller can read or
  modify any record.
- **Client-side gate is cosmetic.** Only `notifications.js` calls `requireAuth()`, and
  it merely redirects based on `localStorage`, which the client controls.
- **Credentials in `application.properties`.** `root`/`root` are committed in plain
  text. A production build would inject them from environment variables or a vault.

### 8.3 Planned hardening (roadmap)

1. Add `spring-boot-starter-security` and a `SecurityFilterChain` denying `/api/**`
   by default.
2. Issue a signed JWT at login; store it in `localStorage`; attach it as a
   `Bearer` header inside `apiRequest`; add a 401 interceptor that redirects to
   `login.html`.
3. Enforce role and ownership: `STUDENT` may only mutate their own profile,
   applications and notifications; `COMPANY` may only mutate their own company,
   their internships, and the statuses of applications to those internships.
4. Move datasource credentials to environment variables
   (`spring.datasource.password=${DB_PASSWORD}`).
5. Replace `WebConfig`'s hard-coded origins with a configurable property.

---

## 9. Frontend Design

### 9.1 Page map

| Page | Purpose | Script |
|---|---|---|
| `index.html` | Meta-refresh redirect to `login.html` | — |
| `login.html` | Email + password sign-in | `api.js`, `auth.js` |
| `register.html` | Account creation with role and `refId` | `api.js`, `auth.js` |
| `dashboard.html` | Six stat cards: Total, Applied, Shortlisted, Interview, Selected, Rejected | `api.js`, `dashboard.js` |
| `students.html` | Student table + add/edit modal + delete | `api.js`, `students.js` |
| `companies.html` | Company table + add/edit modal + delete | `api.js`, `companies.js` |
| `internships.html` | Internship table + add/edit modal + company dropdown | `api.js`, `internships.js` |
| `applications.html` | Application table + add/edit modal + status-update modal | `api.js`, `applications.js` |
| `notifications.html` | Notification table, unread badge, mark-all-as-read | `api.js`, `notifications.js` |

### 9.2 The `api.js` contract

A single `apiRequest(endpoint, options)` wrapper is the only place that touches
`fetch`:

- resolves the URL as `/api` + `endpoint` (same-origin, so the CORS config is not
  exercised in the normal path);
- injects `Content-Type: application/json` and `JSON.stringify`s an object body;
- on a non-2xx response, reads the error body and throws
  `new Error(body.message || "HTTP error! status: " + status)`;
- returns `null` for `204 No Content`, otherwise the parsed JSON;
- logs `API Error: ...` and re-throws, so each page renders its own alert.

Namespaced helpers (`api.students`, `api.companies`, `api.internships`,
`api.applications`, `api.skills`, `api.auth`, `api.notifications`, `api.stats`) keep
endpoint paths out of the page scripts.

### 9.3 State and cross-page conventions

- The logged-in user is stored as JSON under `localStorage` key `interntrack_user`
  and read back with `getCurrentUser()`.
- Every table page renders text through a local `escapeHtml()` helper, so user-supplied
  strings cannot inject markup.
- Status values are rendered as colour-coded badges via `getStatusClass()`:
  `status-applied` (blue), `status-shortlisted` (amber), `status-interview` (indigo),
  `status-selected` (green), `status-rejected` (red).
- `showAlert()` and `escapeHtml()` are currently duplicated per page rather than
  shared in a utility module — a small refactor target.
- `style.css` defines the whole visual language with CSS custom properties
  (`--primary`, `--success`, `--warning`, `--danger`, `--info`, `--bg`, `--card-bg`,
  `--text`, `--text-muted`, `--border`) and a single `@media (max-width: 768px)` rule
  that stacks the nav and the form rows.

---

## 10. Error Handling Strategy

```
Service throws
   ├─ NotFoundException          ─┐
   ├─ DuplicateResourceException  ├─► GlobalExceptionHandler (@ControllerAdvice)
   ├─ IllegalArgumentException   │        ├─ 404 / 409 / 400
   └─ any other Exception       ─┘        └─ 500 (catch-all, registered last)
                                                     │
                                                     ▼
                                     { status, message, timestamp } as JSON
```

Design points:

- **Unchecked exceptions.** Both custom types extend `RuntimeException`, so service
  signatures stay clean and no method is forced to declare `throws`.
- **One advice class.** Registered once for the whole application, so no controller
  needs its own `@ExceptionHandler` and the error contract cannot drift between
  modules.
- **The catch-all is registered last** so it never shadows a specific handler.
- **Deliberate degradation in notifications.** `createNotificationForStudent` wraps
  its body in `try/catch (Exception)` and logs to stderr. A notification failure must
  not roll back or fail a status update the user already performed.
- **No logging in the handler.** Errors surface as JSON only; structured logging is a
  roadmap item so that 500s are observable in production.

---

## 11. Configuration

`src/main/resources/application.properties`

| Property | Value | Note |
|---|---|---|
| `spring.application.name` | `InternTrack` | |
| `spring.datasource.url` | `jdbc:mysql://localhost:3307/interntrack?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC` | Port 3307, DB auto-created |
| `spring.datasource.username` / `.password` | `root` / `root` | Must be replaced outside local development (§8.3) |
| `spring.datasource.driver-class-name` | `com.mysql.cj.jdbc.Driver` | |
| `spring.sql.init.mode` | `always` | Runs `schema.sql` + `data.sql` on every boot |
| `spring.sql.init.schema-locations` | `classpath:schema.sql` | Drops and recreates all tables |
| `spring.sql.init.data-locations` | `classpath:data.sql` | Seeds demo data |
| `spring.jpa.open-in-view` | `false` | Harmless: JPA is not on the classpath |
| `server.port` | `8080` | |
| `springdoc.api-docs.path` | `/api-docs` | |
| `springdoc.swagger-ui.path` | `/swagger-ui.html` | |

> `schema.sql` begins with `DROP TABLE IF EXISTS` in reverse-dependency order, so
> **every restart wipes the database** and reloads the seed set. This is ideal for
> demos and coursework, and must be replaced by a migration tool (Flyway/Liquibase)
> before any real data is stored.

---

## 12. Build and Run

### Prerequisites
Java 17+, Maven 3.8+, MySQL 8.x listening on port 3307.

### Steps

```bash
cd "BSC_InternTrack– By Vivek and Aditya"
mvn clean install
mvn spring-boot:run
```

### Verification

| Check | URL |
|---|---|
| Application | `http://localhost:8080/index.html` → redirects to `login.html` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/api-docs` |
| Health of the API | `http://localhost:8080/api/students` returns 5 seeded students |

### Seeded logins

All five seeded users share the BCrypt hash of the password `password`.

| Email | Role | refId |
|---|---|---|
| `alice@example.com` | STUDENT | `student-001` |
| `bob@example.com` | STUDENT | `student-002` |
| `carol@example.com` | STUDENT | `student-003` |
| `hr@techcorp.com` | COMPANY | `company-001` |
| `careers@datawave.com` | COMPANY | `company-002` |

Seed volume: 5 students, 5 companies, 5 users, 8 internships, 20 skills, 15
student-skill links, 22 internship-skill links, 10 applications, 3 notifications.

---

## 13. Design Trade-offs

| Decision | Alternative rejected | Rationale |
|---|---|---|
| Layered monolith | Microservices | One team, one coursework deliverable; a network hop per entity lookup would be pure overhead at this scale. The package boundaries already isolate the seams if a split is ever needed. |
| `JdbcTemplate` | Spring Data JPA | Predictable SQL for the counting and filtering queries; no N+1 surprises; no lazy-loading proxy complexity. Cost: hand-written `RowMapper`s. |
| Synchronous notification insert | Async events / broker | A status change must be immediately visible in the UI after refresh. A queue would add a component with no benefit at this volume. |
| Static HTML + vanilla JS | React/Vue SPA | Avoids a second toolchain and a build step; CRUD tables and modals do not justify a component framework. |
| `VARCHAR(36)` string PKs | `BIGINT AUTO_INCREMENT` | Readable, environment-portable seed data and a 36-char budget that also holds a prefixed UUID. Cost: client-supplied ids must be validated (they are). |
| No `UserController` | Full user admin API | Out of scope for the stated requirement; `UserService` is exercised through registration and login. |
| `DROP` + reseed on boot | Flyway/Liquibase migrations | Zero setup friction for an evaluator. Deliberately noted as a production blocker in §11. |

---

## 14. Future Enhancements

**Near term**
1. Spring Security filter chain + JWT issuance and `Bearer` attachment in `api.js`.
2. Role and ownership authorisation (FR-08 becomes "a student applies for *their own* profile").
3. Flyway or Liquibase migrations to stop wiping data on restart.
4. `application.properties` → environment variables for datasource credentials.
5. Server-side ID generation for all entities; remove client-side `Date.now()` ids.
6. `@Transactional` on the multi-write service methods (skill mapping, status change + notification) for atomicity.
7. Extract the duplicated `escapeHtml()` / `showAlert()` into a shared `utils.js`.
8. Add JUnit 5 + MockMvc + Testcontainers tests; `spring-boot-starter-test` is already a dependency but no test source set exists yet.

**Medium term**
9. Pagination and sorting on the list endpoints (`/{id}` vs page queries).
10. Search and filter: by skill overlap, location, stipend range, deadline.
11. Skill-match score between a student's skills and an internship's required skills.
12. Resume upload (currently only a `resume_url` string is stored).
13. Email delivery of notifications on top of the in-app centre.
14. CSV / PDF export of the application tracker.
15. An admin role with a moderation panel.

**Longer term**
16. Containerise with Docker, add a CI pipeline that runs `mvn verify` on every PR.
17. Caching for the read-heavy dashboard statistics.
18. Optional service decomposition along the module boundaries in §5.1.

---

## 15. Appendix — Document Map

| File | Contents |
|---|---|
| `SYSTEM_DESIGN.md` | This document — architecture, modules, rules, security, trade-offs |
| `DATABASE_DESIGN.md` | Table-by-table DDL, keys, indexes, ER diagram, seed data |
| `API_REFERENCE.md` | Every endpoint with method, path, body, response and status codes |
| `README.md` | Project overview, feature list, structure, run instructions |
