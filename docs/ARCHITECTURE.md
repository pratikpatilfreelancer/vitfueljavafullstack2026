# System Architecture & Design Documentation

## 1. Waterfall Model — Applied to This Project

The Waterfall Model is a linear, sequential software development process where
each phase must be completed before the next begins. This project followed
that process as follows:

```mermaid
flowchart TD
    A[Requirement Analysis] --> B[System Design]
    B --> C[Implementation / Coding]
    C --> D[Testing]
    D --> E[Deployment]
    E --> F[Maintenance]

    A1["- Identify features: Employee CRUD, Leave CRUD, approval workflow<br/>- Identify tech stack: Spring Boot, Thymeleaf, MySQL, JPA"] -.-> A
    B1["- Design ER diagram (Employee, LeaveRequest)<br/>- Design MVC layers<br/>- Design page flow / wireframes"] -.-> B
    C1["- Build Entities, Repositories, Services, Controllers<br/>- Build Thymeleaf templates"] -.-> C
    D1["- Test CRUD operations<br/>- Test validation rules<br/>- Test approve/reject workflow"] -.-> D
    E1["- Run locally via Maven / VS Code<br/>- Package as a runnable Spring Boot app"] -.-> E
    F1["- Fix bugs<br/>- Add features (e.g. leave balance tracking) in future versions"] -.-> F
```

### Why Waterfall fits a project like this

* Requirements were fixed and well understood upfront (a standard CRUD + approval
  workflow application) — Waterfall works best when requirements are unlikely to
  change mid-project, which is the case for a capstone/academic project.
* Each phase has a clear, demonstrable deliverable (design docs, working code,
  test results), which maps naturally onto how a capstone project is usually
  evaluated phase by phase.
* Unlike Agile, there's no need for iterative sprints or a changing product
  backlog for a project of this fixed, small scope.

---

## 2. Three-Tier / MVC Architecture

```mermaid
flowchart LR
    subgraph Client["Client (Browser)"]
        UI[HTML + Bootstrap<br/>rendered by Thymeleaf]
    end

    subgraph Presentation["Presentation Layer"]
        Controller[Controllers<br/>EmployeeController<br/>LeaveRequestController<br/>DashboardController]
        Templates[Thymeleaf Templates<br/>dashboard.html, employees/*.html, leaves/*.html]
    end

    subgraph Business["Business / Service Layer"]
        Service[Services<br/>EmployeeService<br/>LeaveRequestService]
    end

    subgraph Persistence["Data Access Layer"]
        Repo[Repositories<br/>EmployeeRepository<br/>LeaveRequestRepository<br/>Spring Data JPA]
    end

    subgraph DB["Database"]
        MySQL[(MySQL<br/>employees table<br/>leave_requests table)]
    end

    UI <--> Controller
    Controller --> Templates
    Controller <--> Service
    Service <--> Repo
    Repo <--> MySQL
```

### Layer responsibilities

| Layer | Classes | Responsibility |
|---|---|---|
| Presentation | `*Controller.java`, `templates/*.html` | Handle HTTP requests, pass data to/from templates, no business logic |
| Business/Service | `EmployeeService.java`, `LeaveRequestService.java` | Business rules (e.g. approving/rejecting leave), coordinates repository calls |
| Data Access | `EmployeeRepository.java`, `LeaveRequestRepository.java` | Talk to the database via Spring Data JPA — no manual SQL needed |
| Entity/Model | `Employee.java`, `LeaveRequest.java`, `LeaveStatus.java` | Represent database tables as Java objects (ORM mapping) |

---

## 3. Request Flow Example — Applying for Leave

```mermaid
sequenceDiagram
    participant U as User (Browser)
    participant C as LeaveRequestController
    participant S as LeaveRequestService
    participant R as LeaveRequestRepository
    participant DB as MySQL Database

    U->>C: GET /leaves/apply
    C->>U: Renders leaves/form.html (with employee dropdown)
    U->>C: POST /leaves/save (form data)
    C->>C: Validate form (@Valid)
    C->>S: applyForLeave(leaveRequest)
    S->>R: save(leaveRequest)
    R->>DB: INSERT INTO leave_requests ...
    DB-->>R: Saved row (with generated id)
    R-->>S: LeaveRequest object
    S-->>C: LeaveRequest object
    C->>U: redirect:/leaves
```
