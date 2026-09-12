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
