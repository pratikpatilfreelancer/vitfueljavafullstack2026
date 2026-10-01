# BSC_InternTrack – By Vivek and Aditya

**Project folder:** `BSC_InternTrack– By Vivek and Aditya`
**Contributors:** Vivek and Aditya
**Course:** VIT — B.Sc project submission

## System Design Documentation

| Document | Contents |
|---|---|
| [`docs/SYSTEM_DESIGN.md`](docs/SYSTEM_DESIGN.md) | Architecture, layered design, module breakdown, business rules, security design, trade-offs, roadmap |
| [`docs/DATABASE_DESIGN.md`](docs/DATABASE_DESIGN.md) | ER diagram, table-by-table DDL, keys, indexes, status lifecycle, seed data, DAO mapping |
| [`docs/API_REFERENCE.md`](docs/API_REFERENCE.md) | All 52 REST endpoints with request/response contracts, status codes and worked examples |

---

# InternTrack - Student Internship Application Management System

InternTrack is a web-based application that helps students manage their internship applications in one centralized system. It allows students to create profiles, manage companies, add internships, apply for internships, and track their application status.

## Features

- Student profile management (CRUD)
- Company management (CRUD)
- Internship opportunity management (CRUD)
- Application tracking with status updates
- Dashboard with real-time statistics
- RESTful API with Swagger documentation
- Clean, responsive web UI

## Technology Stack

### Backend
- Java 17
- Spring Boot 3.2.5
- Spring Web
- Spring JDBC / JdbcTemplate
- MySQL
- Maven
- Springdoc OpenAPI (Swagger)

### Frontend
- HTML5
- CSS3
- Vanilla JavaScript

## Project Structure

```
InternTrack/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/internships/
│   │   │   ├── InternTrackApplication.java
│   │   │   ├── config/
│   │   │   │   └── WebConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── StudentController.java
│   │   │   │   ├── CompanyController.java
│   │   │   │   ├── InternshipController.java
│   │   │   │   └── ApplicationController.java
│   │   │   ├── service/
│   │   │   │   ├── StudentService.java
│   │   │   │   ├── StudentServiceImpl.java
│   │   │   │   ├── CompanyService.java
│   │   │   │   ├── CompanyServiceImpl.java
│   │   │   │   ├── InternshipService.java
│   │   │   │   ├── InternshipServiceImpl.java
│   │   │   │   ├── ApplicationService.java
│   │   │   │   └── ApplicationServiceImpl.java
│   │   │   ├── dao/
│   │   │   │   ├── StudentDao.java
│   │   │   │   ├── StudentDaoImpl.java
│   │   │   │   ├── CompanyDao.java
│   │   │   │   ├── CompanyDaoImpl.java
│   │   │   │   ├── InternshipDao.java
│   │   │   │   ├── InternshipDaoImpl.java
│   │   │   │   ├── ApplicationDao.java
│   │   │   │   └── ApplicationDaoImpl.java
│   │   │   ├── model/
│   │   │   │   ├── Student.java
│   │   │   │   ├── Company.java
│   │   │   │   ├── Internship.java
│   │   │   │   └── InternshipApplication.java
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       ├── NotFoundException.java
│   │   │       └── DuplicateResourceException.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── schema.sql
│   │       ├── data.sql
│   │       └── static/
│   │           ├── index.html
│   │           ├── dashboard.html
│   │           ├── students.html
│   │           ├── companies.html
│   │           ├── internships.html
│   │           ├── applications.html
│   │           ├── css/style.css
│   │           └── js/
│   │               ├── api.js
│   │               ├── dashboard.js
│   │               ├── students.js
│   │               ├── companies.js
│   │               ├── internships.js
│   │               └── applications.js
```

## Database Setup

1. Install MySQL Server.
2. Create the database manually or let Spring Boot create it automatically.

```sql
CREATE DATABASE IF NOT EXISTS interntrack;
```

The `schema.sql` and `data.sql` files are executed automatically on startup.

## MySQL Configuration

Update `src/main/resources/application.properties` with your MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/interntrack?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

## How to Run Backend

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL Server

### Steps

```bash
cd InternTrack
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`.

## How to Open Frontend

Open your browser and navigate to:

- `http://localhost:8080/index.html` - Dashboard
- `http://localhost:8080/students.html` - Students
- `http://localhost:8080/companies.html` - Companies
- `http://localhost:8080/internships.html` - Internships
- `http://localhost:8080/applications.html` - Applications

## Swagger URL

OpenAPI/Swagger UI is available at:

- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/swagger-ui/index.html`

API docs JSON: `http://localhost:8080/api-docs`

## API Endpoints

### Students
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/students | Create student |
| GET | /api/students | Get all students |
| GET | /api/students/{id} | Get student by ID |
| GET | /api/students/email/{email} | Get student by email |
| PUT | /api/students/{id} | Update student |
| DELETE | /api/students/{id} | Delete student |

### Companies
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/companies | Create company |
| GET | /api/companies | Get all companies |
| GET | /api/companies/{id} | Get company by ID |
| PUT | /api/companies/{id} | Update company |
| DELETE | /api/companies/{id} | Delete company |

### Internships
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/internships | Create internship |
| GET | /api/internships | Get all internships |
| GET | /api/internships/{id} | Get internship by ID |
| GET | /api/internships/company/{companyId} | Get internships by company |
| PUT | /api/internships/{id} | Update internship |
| DELETE | /api/internships/{id} | Delete internship |

### Applications
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/applications | Create application |
| GET | /api/applications | Get all applications |
| GET | /api/applications/{id} | Get application by ID |
| GET | /api/applications/student/{studentId} | Get applications by student |
| GET | /api/applications/internship/{internshipId} | Get applications by internship |
| GET | /api/applications/status/{status} | Get applications by status |
| PUT | /api/applications/{id} | Update application |
| PUT | /api/applications/{id}/status | Update application status |
| DELETE | /api/applications/{id} | Delete application |

## Sample Data

The project includes `schema.sql` and `data.sql` which create:
- 5 students
- 5 companies
- 8 internships
- 10 applications with various statuses

## Application Statuses

- Applied
- Shortlisted
- Interview
- Selected
- Rejected

## Error Handling

The API returns consistent JSON error responses:

```json
{
    "status": 404,
    "message": "Student not found with id: ...",
    "timestamp": "..."
}
```

## CORS

CORS is configured to allow requests from `http://localhost:8080` for development.

## Future Enhancements

- User authentication and authorization
- Resume upload functionality
- Email notifications
- Advanced filtering and search
- Pagination for large datasets
- Export to CSV/PDF
- Admin panel

## License

This project is created for educational purposes.
