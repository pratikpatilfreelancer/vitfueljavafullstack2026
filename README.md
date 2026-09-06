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
- Multi-institute (multi-tenant) support
