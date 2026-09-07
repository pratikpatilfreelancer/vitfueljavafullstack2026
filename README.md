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
