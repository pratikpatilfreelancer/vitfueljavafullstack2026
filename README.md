<<<<<<< HEAD
# Tourist Management System (TMS)

A robust, enterprise-grade web application built using **Spring Boot 3**, **Spring Data JPA / Hibernate**, and **MySQL**, with dynamic server-side rendering powered by **Thymeleaf**. Designed following strict **Model-View-Controller (MVC)** architectural principles and a classical **Waterfall SDLC** methodology.

---

## 📋 Table of Contents
1. [Project Overview](#project-overview)
2. [Key Features & CRUD Operations](#key-features--crud-operations)
3. [Technology Stack](#technology-stack)
4. [System Architecture & Design](#system-architecture--design)
5. [Database Design & Normalization](#database-design--normalization)
6. [Waterfall SDLC Process](#waterfall-sdlc-process)
7. [Team Roles & Task Breakdown](#team-roles--task-breakdown)
8. [Setup & Installation](#setup--installation)
9. [Project Structure](#project-structure)
10. [Coding Standards & Best Practices](#coding-standards--best-practices)

---

## 🌟 Project Overview
The **Tourist Management System** is designed to streamline travel agency administrative workflows by providing a structured, transactional backend for registering tourists, recording package bookings, tracking destinations, and maintaining relational records.

The primary engineering focus is on **clean backend development**, separation of concerns, robust transaction handling, and relational database integrity.

---

## ⚙️ Key Features & CRUD Operations

| Operation | HTTP Method | Endpoint | Controller Handler | Underlying Database / JPA Action |
| :--- | :---: | :--- | :--- | :--- |
| **Create** | `GET` / `POST` | `/showNewTouristForm`<br>`/saveTourist` | `TouristController.showNewTouristForm()`<br>`TouristController.saveTourist()` | Binds empty `Tourist` to Thymeleaf form; executes `touristRepository.save()` (`INSERT INTO tourists ...`). |
| **Read** | `GET` | `/` | `TouristController.viewHomePage()` | Invokes `touristService.getAllTourists()`; returns dynamic record table via `th:each`. |
| **Update** | `GET` / `POST` | `/showFormForUpdate/{id}`<br>`/saveTourist` | `TouristController.showFormForUpdate()`<br>`TouristController.saveTourist()` | Fetches record by ID (`findById()`); populates edit form; hidden field `th:field="*{id}"` triggers SQL `UPDATE`. |
| **Delete** | `GET` | `/deleteTourist/{id}` | `TouristController.deleteTourist()` | Triggers `touristService.deleteTouristById(id)` (`DELETE FROM tourists WHERE id = ?`); redirects to `/`. |

---

## 🛠️ Technology Stack

* **Language:** Java 21 (LTS)
* **Framework:** Spring Boot 3.x
* **Data Access & ORM:** Spring Data JPA, Hibernate 6
* **Database Engine:** MySQL 8.x
* **Template Engine (Frontend):** Thymeleaf, HTML5, CSS3 / Bootstrap
* **Build & Dependency Tool:** Apache Maven
* **Application Server:** Embedded Apache Tomcat

---

## 🏛️ System Architecture & Design

The application enforces a multi-tier **Model-View-Controller (MVC)** architectural pattern:

```text
[ Browser / Client ]
        │
        ▼ (HTTP Requests: GET, POST)
┌────────────────────────────────────────────────────────┐
│               Spring Boot Controller Layer             │
│               (TouristController.java)                 │
└───────────────────────┬────────────────────────────────┘
                        │
                        ▼ (Business Invocations)
┌────────────────────────────────────────────────────────┐
│                 Service Layer & Logic                  │
│       (TouristService.java / TouristServiceImpl.java)   │
└───────────────────────┬────────────────────────────────┘
                        │
                        ▼ (ORM Repository Operations)
┌────────────────────────────────────────────────────────┐
│               Data Access Layer (JPA / DAO)            │
│               (TouristRepository.java)                 │
└───────────────────────┬────────────────────────────────┘
                        │
                        ▼ (SQL Invocations / Dialect)
┌────────────────────────────────────────────────────────┐
│                  Relational Database                   │
│                     (MySQL DB)                         │
└────────────────────────────────────────────────────────┘
```

1. **Model Layer (`Tourist.java`, `Booking.java`):** `@Entity` mapped classes defining schemas, primary keys (`@Id`, `@GeneratedValue`), and relational mappings (`@ManyToOne`).
2. **Repository Layer (`TouristRepository.java`):** Extends `JpaRepository<Tourist, Long>`, exposing boilerplate persistence commands (`save()`, `findById()`, `findAll()`, `deleteById()`).
3. **Service Layer (`TouristServiceImpl.java`):** Encapsulates business logic, null-safety checks via `Optional<T>`, and transactional boundaries.
4. **Controller Layer (`TouristController.java`):** Routes incoming HTTP traffic, manages model bindings (`org.springframework.ui.Model`), and routes view names.
5. **View Layer (Thymeleaf Templates):** Server-rendered templates (`index.html`, `new_tourist.html`, `update_tourist.html`).

---

## 🗄️ Database Design & Normalization

The system strictly avoids monolithic or flat tables to eliminate update anomalies and duplication. The schema satisfies **Third Normal Form (3NF)**:

### 1. Entity Breakdown (2 Normalized Tables)
* **`tourists` Table:**
  * `id` (BIGINT, Primary Key, Auto Increment)
  * `name` (VARCHAR(100), NOT NULL)
  * `email` (VARCHAR(100), UNIQUE, NOT NULL)
  * `phone` (VARCHAR(20))
  * `country` (VARCHAR(50))
* **`bookings` Table:**
  * `booking_id` (BIGINT, Primary Key, Auto Increment)
  * `tourist_id` (BIGINT, Foreign Key referencing `tourists(id)`, ON DELETE CASCADE)
  * `destination` (VARCHAR(100), NOT NULL)
  * `travel_date` (DATE, NOT NULL)
  * `package_price` (DECIMAL(10,2), NOT NULL)

### 2. Normalization Process
* **1NF:** Atomic attribute fields (no multi-valued destination sets stored in a single cell).
* **2NF:** Fully functional dependencies on the primary key without partial dependency.
* **3NF:** No transitive dependencies (booking details depend exclusively on `booking_id`, while tourist identity depends on `tourist_id`).

### 3. Join Query Implementation
```sql
SELECT 
    t.id AS tourist_id,
    t.name,
    t.email,
    b.booking_id,
    b.destination,
    b.travel_date,
    b.package_price
FROM tourists t
INNER JOIN bookings b ON t.id = b.tourist_id
ORDER BY b.travel_date DESC;
```

---

## 🔄 Waterfall SDLC Process

1. **Requirements & Scope:** Documented functional specifications for tourist onboarding and booking management.
2. **System Design:** Designed the relational 3NF database schema, class diagrams, and Spring Boot package boundaries.
3. **Implementation:** Developed the JPA entities, data repositories, business services, and MVC controllers.
4. **Integration & Testing:** Performed unit testing with JUnit 5, Mockito, and manual browser validation of CRUD flows.
5. **Deployment & Review:** Packaged executable JAR and finalized presentation artifacts.

---

## 👥 Team Roles & Task Breakdown

| Domain / Area | Key Deliverables | Member 1 | Member 2 |
| :--- | :--- | :---: | :---: |
| **Backend: Database & JPA** | MySQL Schema (3NF), Entity design (`@Entity`), Spring Data JPA Repositories | **Lead Contributor** | Collaborator |
| **Backend: Service & Controller** | Business layer validation, `Optional` handling, `@GetMapping`/`@PostMapping` routing | Collaborator | **Lead Contributor** |
| **Frontend UI** | Dynamic Thymeleaf templates (`th:each`, `th:field`), form design | — | **Lead Contributor** |
| **Documentation & Architecture**| Architecture flows, Waterfall lifecycle, schema normalization report | **Lead Contributor** | — |
| **Presentation** | Technical slide deck, code walkthroughs, live execution demo | — | **Lead Contributor** |

---

## 🚀 Setup & Installation

### Prerequisites
* **Java Development Kit (JDK):** Version 21 or higher
* **MySQL Server:** Version 8.0+
* **Maven:** Version 3.8+ (or use the included `./mvnw`)

### 1. Database Configuration
Open MySQL CLI / Workbench and create the database:
```sql
CREATE DATABASE tourist_db;
```

Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tourist_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

### 2. Build & Run
```bash
# Clone the repository
git clone https://github.com/your-username/tourist-management-system.git
cd tourist-management-system

# Build application
./mvnw clean install

# Run application
./mvnw spring-boot:run
```

Access the dashboard at `http://localhost:8080/`.

---

## 📁 Project Structure

```text
tourist-management-system/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── tms/
    │   │           ├── TouristManagementSystemApplication.java
    │   │           ├── controller/
    │   │           │   └── TouristController.java
    │   │           ├── model/
    │   │           │   ├── Tourist.java
    │   │           │   └── Booking.java
    │   │           ├── repository/
    │   │           │   ├── TouristRepository.java
    │   │           │   └── BookingRepository.java
    │   │           └── service/
    │   │               ├── TouristService.java
    │   │               └── TouristServiceImpl.java
    │   └── resources/
    │       ├── application.properties
    │       └── templates/
    │           ├── index.html
    │           ├── new_tourist.html
    │           └── update_tourist.html
    └── test/
        └── java/
            └── com/
                └── tms/
                    └── TouristManagementSystemApplicationTests.java
```

---

## 📐 Coding Standards & Best Practices

* **Meaningful Naming Conventions:** PascalCase for class names (`TouristServiceImpl`), camelCase for methods/variables (`touristRepository`, `getAllTourists`).
* **Clean Code & Comments:** Every service interface method is documented using standard Javadoc.
* **Separation of Concerns:** Business checks are isolated inside the Service tier, never inside the UI controller.
* **Defensive Programming:** Proper usage of `java.util.Optional` avoids `NullPointerException` risks during database lookups.
**README.md**

# vitfueljavafullstack2026
VIT full stack java repo CS and AIML project
=======
# Online Exam Evaluation System

A Spring Boot–based web application that allows admins to create exams with multiple question types (MCQ, True/False) and enables candidates to attempt them online, with automatic evaluation and score generation. Built using Spring Boot, JPA, and a normalized relational database, it demonstrates core OOP concepts including inheritance, polymorphism, aggregation, interfaces, and exception handling, along with full CRUD functionality.

## Features

- Create, update, and delete exams
- Add, edit, and delete MCQ and True/False questions
- Candidates can attempt exams through a web interface
- Automatic evaluation and score generation on submission
- View exam results and history
- REST API for all core operations, tested via Postman
- Interactive API documentation via Swagger/OpenAPI

## Tech Stack

- **Backend:** Spring Boot
- **Data Layer:** Spring Data JPA (Hibernate)
- **Frontend:** Thymeleaf, HTML, CSS
- **Database:** MySQL
- **API Testing:** Postman
- **API Documentation:** Swagger / OpenAPI
- **End-to-End Testing:** Selenium / Playwright

## OOP Concepts Demonstrated

| Concept | Where |
|---|---|
| Inheritance | `Question` (abstract) → `MCQQuestion`, `TrueFalseQuestion` |
| Polymorphism (runtime) | `evaluate()` overridden per question type, called via base `Question` reference |
| Polymorphism (compile-time) | Overloaded question creation methods |
| Aggregation | `Exam` HAS-A `Question` list; `Exam` HAS-A `Attempt` list |
| Encapsulation | Private fields with public getters/setters across all entities |
| Exception Handling | Try/catch blocks in service layer for safe evaluation and lookups |
| Static/Final | Constants and shared values where applicable |
| Downcasting | Used when editing type-specific question fields (MCQ/True-False) |

## Architecture

```
Presentation (Thymeleaf) 
        ↓
Controller (REST + Page Controllers)
        ↓
Service (Business Logic)
        ↓
Repository (Spring Data JPA)
        ↓
Database (MySQL)
```

## Database Schema

**Tables:** `exam`, `question`, `attempt`, `mcqquestion_options`

- `exam` (1) ── (many) `question`
- `exam` (1) ── (many) `attempt`

Normalized into separate tables to avoid data duplication (2NF/3NF), with foreign key relationships enforced at the database level.

## Getting Started

### Prerequisites
- Java 17
- Maven
- MySQL Server running locally

### Setup

1. Clone or download this project
2. Create a database:
   ```sql
   CREATE DATABASE examdb;
   ```
3. Configure `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/examdb
   spring.datasource.username=root
   spring.datasource.password=yourpassword
   spring.jpa.hibernate.ddl-auto=update
   ```
4. Run the application:
   ```
   mvn spring-boot:run
   ```
5. Open your browser at:
   ```
   http://localhost:8080/
   ```

## API Documentation

Once the app is running, view interactive API docs at:
```
http://localhost:8080/swagger-ui/index.html
```

## Page Routes

| URL | Description |
|---|---|
| `/` | Home page |
| `/exams` | View all exams |
| `/exams/create` | Create a new exam |
| `/exams/{id}/edit` | Edit an exam |
| `/exams/{id}/questions` | Add/edit/delete questions for an exam |
| `/exams/{id}/attempt` | Attempt an exam as a candidate |

## REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/exams` | Create exam |
| GET | `/api/exams` | List all exams |
| GET | `/api/exams/{id}` | Get exam by ID |
| PUT | `/api/exams/{id}` | Update exam |
| DELETE | `/api/exams/{id}` | Delete exam |
| POST | `/api/exams/{id}/questions/mcq` | Add MCQ question |
| POST | `/api/exams/{id}/questions/truefalse` | Add True/False question |
| DELETE | `/api/questions/{id}` | Delete a question |
| POST | `/api/exams/{id}/attempt` | Submit an exam attempt |

## Project Structure

```
src/main/java/com/examapp/examapp/
  entity/        Exam, Question, MCQQuestion, TrueFalseQuestion, Attempt
  repository/    ExamRepository, QuestionRepository, AttemptRepository
  service/       ExamService, QuestionService, AttemptService
  controller/    REST controllers and Thymeleaf page controllers
  
src/main/resources/
  templates/     Thymeleaf HTML pages
  static/        CSS
  application.properties
```

## Team

| Member | Responsibility |
|---|---|
| [Your Name] | Backend (entities, services, controllers) |
| [Partner Name] | Database design, frontend integration |

## Development Process

This project followed the Waterfall model:
1. Requirement Analysis — PRD and feature list
2. System Design — class diagram, ER diagram, architecture diagram
3. Implementation — entity, repository, service, controller layers
4. Testing — Postman for API testing, Selenium/Playwright for end-to-end testing
5. Deployment — final working build

# vitfueljavafullstack2026
VIT full stack java repo CS and AIML project
# Attendance Management System (AMS)

A full-stack web application for schools, colleges, and institutes to record and manage daily student attendance. Built with **Spring Boot 3**, **Thymeleaf**, **Spring Data JPA**, **MySQL**, and **Spring Security**.

## Features

- **Role-based access** — Admin and Teacher roles with Spring Security
- **Admin CRUD** — Manage teachers, students, and batches
- **Attendance marking** — Mark attendance for a whole batch (checkbox list) or mark absentees only
- **Business rules** — Future-date rejection, duplicate attendance prevention, unique enrollment/employee numbers
- **Reports** — Monthly student-wise, per-batch date range, and attendance-percentage (flags students < 75%)
- **Export** — Download reports as **Excel** (Apache POI) or **PDF** (OpenPDF)
- **Responsive UI** — Thymeleaf + Bootstrap 5, mobile-friendly
- **BCrypt passwords** — Secure password hashing, never stored in plain text
- **Data seeder** — Auto-creates sample data on first run for easy testing

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.2.5 |
| Frontend | Thymeleaf, Bootstrap 5 |
| Database | MySQL 8+ (H2 for tests) |
| Security | Spring Security 6, BCrypt |
| ORM | Spring Data JPA / Hibernate |
| Reports | Apache POI (Excel), OpenPDF (PDF) |
| Build | Maven |
| Tests | JUnit 5, Mockito |

## Project Structure

```
com.ams
 ├── config/         SecurityConfig, CustomLoginSuccessHandler, DataSeeder, WebConfig
 ├── controller/     AuthController, AdminDashboardController, TeacherController,
 │                   StudentController, BatchController, AttendanceController,
 │                   TeacherDashboardController, TeacherSettingsController,
 │                   AdminReportController, TeacherReportController
 ├── entity/         User, Batch, Student, Attendance
 ├── repository/     UserRepository, BatchRepository, StudentRepository, AttendanceRepository
 ├── service/        UserService, BatchService, StudentService, AttendanceService,
 │                   ReportService, CustomUserDetailsService
 ├── service/impl/   All service implementations
 ├── dto/            TeacherDto, StudentDto, BatchDto, AttendanceFormDto,
 │                   ReportFilterDto, StudentAttendanceStats
 ├── exception/      ResourceNotFoundException, DuplicateRecordException,
 │                   FutureDateException, GlobalExceptionHandler
 └── util/           ExcelReportUtil, PdfReportUtil
```

## Setup & Run

### Prerequisites

- Java 17+ (tested with Java 21)
- Maven 3.8+
- MySQL 8+ running on `localhost:3306`

### 1. Create the database

```bash
mysql -u root -p -e "CREATE DATABASE ams_db;"
```

Or use the provided `schema.sql`:
```bash
mysql -u root -p < schema.sql
```

### 2. Configure database connection

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ams_db
spring.datasource.username=root
spring.datasource.password=yourpassword
```

### 3. Build and run

```bash
mvn clean package
java -jar target/attendance-management-system-0.0.1-SNAPSHOT.jar
```

Or run directly with Maven:
```bash
mvn spring-boot:run
```

### 4. Access the application

Open [http://localhost:8080/login](http://localhost:8080/login)

### Default Credentials (DataSeeder)

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Teacher | `john.smith` | `teacher123` |
| Teacher | `jane.doe` | `teacher123` |

The DataSeeder also creates 3 sample batches (CS101, MATH201, PHY101) and 9 sample students.

## Route Map

| Route | Role | Description |
|-------|------|-------------|
| `/login` | Public | Login form |
| `/admin/dashboard` | ADMIN | Stats overview (teacher/student/batch counts) |
| `/admin/teachers` | ADMIN | List/CRUD teachers |
| `/admin/students` | ADMIN | List/CRUD students |
| `/admin/batches` | ADMIN | List/CRUD batches |
| `/admin/reports` | ADMIN | Generate reports for any batch, export Excel/PDF |
| `/teacher/dashboard` | TEACHER | List of assigned batches |
| `/teacher/batches/{id}/attendance` | TEACHER | Mark attendance for a batch on a date |
| `/teacher/batches/{id}/attendance/absentees` | TEACHER | Mark absentees only |
| `/teacher/reports` | TEACHER | Reports scoped to own batches |
| `/teacher/settings` | TEACHER | Change password |

## Reports

- **Student-wise report** — Present/absent counts per student for a batch and date range
- **Monthly report** — Same as student-wise, scoped to a specific month
- **Percentage report** — Flags students below 75% attendance threshold
- **Export** — All reports downloadable as `.xlsx` (Excel) or `.pdf` (PDF)

## Running Tests

```bash
mvn test
```

Tests use H2 in-memory database (no MySQL required):
- `AttendanceServiceTest` — Unit tests with Mockito (future-date rejection, duplicate prevention, batch/absentee marking)
- `AttendanceRepositoryTest` — Integration tests (queries, unique constraints)
- `StudentRepositoryTest` — Integration tests (find-by queries, unique enrollment enforcement)

## Design Assumptions

1. **Single batch per student** — Matches the spec's FK design (student has one batch_id).
2. **"Mark absentees only"** — Checked students are marked ABSENT; all unchecked students in the batch are marked PRESENT.
3. **Attendance correction** — Same day only; once attendance is marked for a student+date, it cannot be re-marked (enforced by unique constraint).
4. **H2 for tests** — Tests use H2 in-memory DB so `mvn test` works without a running MySQL instance.
5. **`ddl-auto=update`** — JPA auto-creates/updates tables. The `schema.sql` file is provided for manual setup.

## Future Enhancements

- Email/SMS notices for students below 75% attendance
- Biometric / QR-code based attendance capture
- REST API layer + mobile app
>>>>>>> 1a891d395de20d1d764cb9e03ca74d214ff23a66
