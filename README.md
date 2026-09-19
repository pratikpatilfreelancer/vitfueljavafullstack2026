# Online Food Ordering System — Java Swing + Spring Boot

This project combines the original Java Swing/MySQL application with a Spring Boot REST backend.

## Run the application

### Option A — Start the complete desktop application

From the project folder:

```powershell
mvn exec:java
```

`MainApp` starts the Spring Boot backend on `http://localhost:8080` and then opens the Swing UI. The Swing UI communicates with the backend through REST APIs for login, registration, food/menu operations, checkout, order history, and admin order updates.

### Option B — Start only the backend

```powershell
mvn spring-boot:run
```

Backend URL:

```text
http://localhost:8080
```

## REST endpoints

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/admin/login`
- `GET /api/food`
- `GET /api/food?search=pizza`
- `POST /api/food`
- `PUT /api/food/{id}`
- `DELETE /api/food/{id}`
- `POST /api/orders`
- `GET /api/orders/customer/{customerId}`
- `GET /api/orders`
- `PATCH /api/orders/{orderId}/status?status=DELIVERED`

## Database

Make sure MySQL is running and configure the existing `DatabaseConfig.java` credentials before using the application.
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
