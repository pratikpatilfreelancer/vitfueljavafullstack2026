# Build Prompt — Attendance Management System

Copy everything below the line into Claude Code, Claude (with a repo attached), or any coding-capable AI assistant to generate the full project. It references the companion spec file `ATTENDANCE_SYSTEM_SPEC.md` — attach that file alongside this prompt (or paste its contents in) so the assistant has the ERD, schema, and route map.

---

<build_request>

You are building a complete, runnable **Attendance Management System** as a Spring Boot web application. Follow the attached specification document (`ATTENDANCE_SYSTEM_SPEC.md`) exactly for entities, database schema, routes, and architecture. Build it in full — do not stub out modules.

<tech_stack>
- Java 17, Spring Boot 3.x
- Frontend: Thymeleaf (server-rendered HTML) + Bootstrap 5 for styling
- Data access: Spring Data JPA (Hibernate) over MySQL
- Spring Security for authentication (roles: ADMIN, TEACHER)
- Maven build
- Apache POI for Excel export, OpenPDF (or iText) for PDF export
- JUnit 5 + Mockito for tests
</tech_stack>

<architecture>
Use a layered architecture: Controller → Service (interface + impl) → Repository → MySQL.
Package base: `com.ams`, with sub-packages `config`, `controller`, `entity`, `repository`, `service`, `service.impl`, `dto`, `exception`, `util`.
Follow the ER diagram and SQL schema in the spec exactly (users, batch, student, attendance tables).
</architecture>

<functional_scope>
Implement full CRUD for:
1. **Teachers** (Admin-only) — add/edit/delete/list, unique employee/enrollment number.
2. **Students** (Admin-only) — add/edit/delete/list, assign to a batch, unique enrollment number.
3. **Batches** (Admin-only) — batch code, subject, assigned teacher, days (e.g. MWF/TTS), timing slot.
4. **Attendance** (Teacher-only, scoped to their own batches):
   - Mark attendance for a whole batch on a given date (checkbox list of students).
   - Mark attendance for an individual student.
   - "Mark absentees only" mode.
   - Reject marking attendance for a future date (show a validation error).
   - Prevent duplicate attendance for the same student + date (DB unique constraint + service-level check).

Also implement:
- Login page + Spring Security config (BCrypt password hashing, role-based route protection, custom login-success redirect by role).
- Admin dashboard: batch/student/teacher counts, quick links.
- Teacher dashboard: list of assigned batches only.
- Reports module: monthly student-wise present/absent report, per-batch report with custom date range, attendance-percentage report flagging students under 75%. Support Excel and PDF export via download endpoints.
- Global exception handling (`@ControllerAdvice`) with friendly error pages.
- Bean Validation annotations on all form DTOs, with Thymeleaf error display.
</functional_scope>

<deliverables>
Produce, as actual files (not just descriptions):
1. Full Maven project (`pom.xml` with all dependencies listed above).
2. All entity classes matching the schema in the spec.
3. Spring Data JPA repository interfaces for each entity.
4. Service interfaces + implementations containing the business rules above (future-date rejection, role scoping, 75% threshold, duplicate prevention).
5. Controllers for every route listed in the spec's "Page / Route Map" section.
6. Thymeleaf templates for every controller view (login, admin dashboard, teacher dashboard, CRUD list/form pages for teacher/student/batch, attendance-marking page, absentee-marking page, report pages), using a shared layout fragment and Bootstrap 5.
7. `SecurityConfig` enforcing `/admin/**` for ADMIN and `/teacher/**` for TEACHER.
8. `application.properties` with placeholder MySQL connection settings matching the spec.
9. A `schema.sql` or JPA-generated schema consistent with the spec's SQL.
10. A `DataSeeder` (CommandLineRunner) that creates one default admin and one sample teacher/batch/student on first run, for easy testing.
11. Unit tests for at least the attendance service (future-date rejection, duplicate prevention) and one repository.
12. A top-level `README.md` with setup/run instructions (can reuse Section 10 of the spec).

Build iteratively: set up the Maven skeleton and entities first, then repositories, then services with business rules, then security, then controllers, then templates, then reports, then tests. After generating, review that every route in the spec's route map actually resolves to a controller method and template.
</deliverables>

<constraints>
- Do not use an in-memory database; target MySQL as specified.
- Do not skip validation — every user-facing form needs both client-visible and server-side validation.
- Keep controllers thin; business logic belongs in services.
- Passwords must never be stored or logged in plain text.
</constraints>

</build_request>
