# InternTrack — Database Design

**Engine:** MySQL 8.x
**Database:** `interntrack`
**Script:** `src/main/resources/schema.sql` (structure) and `data.sql` (seed)
**Execution:** automatic on every application start (`spring.sql.init.mode=always`)

---

## 1. Conventions

| Rule | Value | Reason |
|---|---|---|
| Primary key type | `VARCHAR(36)` | Fits `student-001` style seeded ids and `notif-<uuid>`; portable across environments |
| Surrogate keys | none | Every entity is identified by a natural, caller-supplied key |
| Audit column | `created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP` on all 9 tables | Row provenance without extra application code |
| Mutation audit | `applications.updated_at TIMESTAMP ... ON UPDATE CURRENT_TIMESTAMP` | The only table whose rows change after insert |
| Foreign keys | 9, all `ON DELETE CASCADE` | Deleting a parent never orphans children |
| Soft delete | none | Out of scope; the tracker is a personal tool |
| Normalisation | 3NF | Skills are shared across students and internships, hence the two junction tables |
| Indexes | implicit on PKs, FKs, and the three `UNIQUE` keys | No table exceeds demo scale; see §6 |

---

## 2. Entity–Relationship diagram

```
                          ┌──────────────┐
                          │    users     │
                          │  user_id  PK │
                          │  email    UQ │
                          │  password    │
                          │  role        │  role ∈ {STUDENT, COMPANY}
                          │  ref_id      │◄── soft link, no FK
                          └──────┬───────┘
                                 │ 1
                                 │
                                 │ N
                          ┌──────┴───────┐        ┌─────────────┐
          ┌───────────────►│ notifications│        │   skills    │
          │  user_id  FK   │notification_ │        │ skill_id PK │
          │                │    id    PK  │        │ skill_name  │
          │                │ message      │        │      UQ     │
          │                │ type         │        │ category    │
          │                │ is_read      │        └──────┬──────┘
          │                └──────────────┘               │
          │                                  ┌────────────┴────────────┐
          │                    ┌─────────────┴──────────┐           │
          │                    │   student_skills       │  internship_skills
          │                    │ student_id  PK/FK      │  internship_id PK/FK
          │                    │ skill_id    PK/FK      │  skill_id      PK/FK
          │                    └─────────────┬──────────┘           └────────────┬──────────┘
          │                                  │ N                                │ N
          │                            ┌─────┴──────┐                   ┌─────┴──────────┐
          │                            │  students  │                   │  internships   │
          │                            │student_id  │                   │internship_id   │
          │                            │    PK      │                   │     PK         │
          │                            │ email   UQ │                   │ company_id FK  │
          │                            └─────┬──────┘                   └─────┬──────────┘
          │                                  │ 1                              │ 1
          │                                  │                                │
          │                                  │ N                              │ N
          │                            ┌─────┴────────────────────────────────┴──┐
          └───────────────────────────►│              applications              │
                                       │        application_id        PK        │
                                       │        student_id           FK        │
                                       │        internship_id         FK        │
                                       │        application_date                │
                                       │        status  (default 'Applied')    │
                                       │        notes                           │
                                       │ UQ (student_id, internship_id)         │
                                       └────────────────────────────────────────┘

┌──────────────┐        ┌──────────────┐
│  companies   │ 1    N │  internships  │
│ company_id PK│───────►│ company_id FK│
│ company_name │        └──────────────┘
│ location     │
│ industry     │
│ website      │
│ contact_email│
└──────────────┘
```

**Legend:** `PK` primary key · `FK` foreign key · `UQ` unique constraint

**Reference integrity summary**

| Child column | Parent | On delete |
|---|---|---|
| `internships.company_id` | `companies.company_id` | CASCADE |
| `student_skills.student_id` | `students.student_id` | CASCADE |
| `student_skills.skill_id` | `skills.skill_id` | CASCADE |
| `internship_skills.internship_id` | `internships.internship_id` | CASCADE |
| `internship_skills.skill_id` | `skills.skill_id` | CASCADE |
| `applications.student_id` | `students.student_id` | CASCADE |
| `applications.internship_id` | `internships.internship_id` | CASCADE |
| `notifications.user_id` | `users.user_id` | CASCADE |
| `users.ref_id` | *(none — soft link)* | not enforced |

`users.ref_id` is intentionally not a foreign key: a single `users` row points at
either a `students` row or a `companies` row depending on `role`, and a single FK
cannot express that polymorphism. The application resolves it in code.

---

## 3. Table definitions

### 3.1 `users`

| Column | Type | Null | Key | Default | Notes |
|---|---|---|---|---|---|
| `user_id` | `VARCHAR(36)` | NO | PK | — | `user-` + first 8 chars of a UUID, generated in `AuthService.register` |
| `email` | `VARCHAR(100)` | NO | UNIQUE | — | Login identity |
| `password` | `VARCHAR(255)` | NO | — | — | BCrypt hash, cost 10 — 60 chars, sized generously |
| `role` | `VARCHAR(20)` | NO | — | — | `STUDENT` or `COMPANY` |
| `ref_id` | `VARCHAR(36)` | YES | — | — | Soft link to `students` or `companies` |
| `created_at` | `TIMESTAMP` | YES | — | `CURRENT_TIMESTAMP` | |

### 3.2 `students`

| Column | Type | Null | Key | Default | Notes |
|---|---|---|---|---|---|
| `student_id` | `VARCHAR(36)` | NO | PK | — | e.g. `student-001`, or `student-<epochMillis>` from the browser |
| `name` | `VARCHAR(100)` | NO | — | — | |
| `email` | `VARCHAR(100)` | NO | UNIQUE | — | Duplicate check in `StudentServiceImpl` |
| `phone` | `VARCHAR(20)` | YES | — | — | |
| `college` | `VARCHAR(100)` | YES | — | — | |
| `course` | `VARCHAR(100)` | YES | — | — | e.g. `Computer Science` |
| `year` | `INT` | YES | — | — | Year of study |
| `resume_url` | `VARCHAR(255)` | YES | — | — | Link only; no file upload in this version |
| `created_at` | `TIMESTAMP` | YES | — | `CURRENT_TIMESTAMP` | |

> `Student` also exposes a transient `skills` field in Java. It is **not** a column —
> skills are read through the `student_skills` junction table.

### 3.3 `companies`

| Column | Type | Null | Key | Default | Notes |
|---|---|---|---|---|---|
| `company_id` | `VARCHAR(36)` | NO | PK | — | |
| `company_name` | `VARCHAR(100)` | NO | — | — | |
| `location` | `VARCHAR(100)` | YES | — | — | |
| `industry` | `VARCHAR(100)` | YES | — | — | |
| `website` | `VARCHAR(255)` | YES | — | — | |
| `contact_email` | `VARCHAR(100)` | YES | — | — | No uniqueness constraint in this version |
| `created_at` | `TIMESTAMP` | YES | — | `CURRENT_TIMESTAMP` | |

### 3.4 `internships`

| Column | Type | Null | Key | Default | Notes |
|---|---|---|---|---|---|
| `internship_id` | `VARCHAR(36)` | NO | PK | — | |
| `company_id` | `VARCHAR(36)` | NO | FK → `companies.company_id` | — | `ON DELETE CASCADE` |
| `title` | `VARCHAR(100)` | NO | — | — | |
| `description` | `TEXT` | YES | — | — | |
| `location` | `VARCHAR(100)` | YES | — | — | |
| `duration` | `VARCHAR(50)` | YES | — | — | Free text, e.g. `3 Months` |
| `stipend` | `DECIMAL(10,2)` | YES | — | — | Mapped as `double` in `Internship` |
| `application_deadline` | `DATE` | YES | — | — | Mapped to a `String` in the model |
| `created_at` | `TIMESTAMP` | YES | — | `CURRENT_TIMESTAMP` | |

> `Internship` also exposes a transient `requiredSkills` field in Java. It is **not**
> a column — required skills are read through `internship_skills`.

### 3.5 `skills`

| Column | Type | Null | Key | Default | Notes |
|---|---|---|---|---|---|
| `skill_id` | `VARCHAR(36)` | NO | PK | — | |
| `skill_name` | `VARCHAR(100)` | NO | UNIQUE | — | The natural key used for duplicate detection |
| `category` | `VARCHAR(100)` | YES | — | — | Programming / Framework / Database / Cloud / AI-ML / Tools / Frontend |
| `created_at` | `TIMESTAMP` | YES | — | `CURRENT_TIMESTAMP` | |

### 3.6 `student_skills` (junction)

| Column | Type | Null | Key | Default |
|---|---|---|---|---|
| `student_id` | `VARCHAR(36)` | NO | PK part 1, FK → `students` | — |
| `skill_id` | `VARCHAR(36)` | NO | PK part 2, FK → `skills` | — |
| `created_at` | `TIMESTAMP` | YES | — | `CURRENT_TIMESTAMP` |

Composite primary key `(student_id, skill_id)` is what makes the mapping idempotent
at the storage level, in addition to the in-service duplicate check.

### 3.7 `internship_skills` (junction)

| Column | Type | Null | Key | Default |
|---|---|---|---|---|
| `internship_id` | `VARCHAR(36)` | NO | PK part 1, FK → `internships` | — |
| `skill_id` | `VARCHAR(36)` | NO | PK part 2, FK → `skills` | — |
| `created_at` | `TIMESTAMP` | YES | — | `CURRENT_TIMESTAMP` |

### 3.8 `applications`

| Column | Type | Null | Key | Default | Notes |
|---|---|---|---|---|---|
| `application_id` | `VARCHAR(36)` | NO | PK | — | |
| `student_id` | `VARCHAR(36)` | NO | FK → `students.student_id` | — | `ON DELETE CASCADE` |
| `internship_id` | `VARCHAR(36)` | NO | FK → `internships.internship_id` | — | `ON DELETE CASCADE` |
| `application_date` | `DATE` | YES | — | — | |
| `status` | `VARCHAR(50)` | YES | — | `'Applied'` | Whitelisted in `ApplicationServiceImpl`: `Applied`, `Shortlisted`, `Interview`, `Selected`, `Rejected` |
| `notes` | `TEXT` | YES | — | — | Free-text recruiter/recap notes |
| `created_at` | `TIMESTAMP` | YES | — | `CURRENT_TIMESTAMP` | |
| `updated_at` | `TIMESTAMP` | YES | — | `CURRENT_TIMESTAMP ON UPDATE` | |

`UNIQUE KEY unique_application (student_id, internship_id)` — a student can apply to
a given internship exactly once.

### 3.9 `notifications`

| Column | Type | Null | Key | Default | Notes |
|---|---|---|---|---|---|
| `notification_id` | `VARCHAR(36)` | NO | PK | — | `notif-` + full UUID |
| `user_id` | `VARCHAR(36)` | NO | FK → `users.user_id` | — | `ON DELETE CASCADE` |
| `message` | `TEXT` | NO | — | — | Rendered verbatim in the UI |
| `type` | `VARCHAR(50)` | YES | — | — | `STATUS_UPDATE` in the current implementation |
| `is_read` | `BOOLEAN` | YES | — | `FALSE` | Drives the unread badge |
| `created_at` | `TIMESTAMP` | YES | — | `CURRENT_TIMESTAMP` | |

---

## 4. Application status lifecycle

```
   ┌─────────┐
   │ Applied │  (default on create)
   └────┬────┘
        │
        ├──► ┌────────────┐
        ├──► │ Shortlisted│
        │    └─────┬──────┘
        │          ├──► ┌───────────┐
        │          ├──► │ Interview │
        │          │    └─────┬─────┘
        │          │          ├──► ┌─────────┐
        │          │          └──► │ Selected│  (terminal, positive)
        │          └──► ┌──────────┐
        └─────────────► │ Rejected │  (terminal, negative)
                         └──────────┘
```

The five values are validated against `ALLOWED_STATUSES` in
`ApplicationServiceImpl` on both create and update; anything else is a 400. Any
transition between the five is currently permitted — the diagram shows the intended
narrative path, not a hard state machine. Enforcing legal edges is a roadmap item.

---

## 5. Seed data (`data.sql`)

| Table | Rows | Identifier range | Notes |
|---|---|---|---|
| `students` | 5 | `student-001` … `student-005` | Alice Johnson … Eve Davis |
| `companies` | 5 | `company-001` … `company-005` | TechCorp, DataWave, CloudSys, NextGen, SoftBridge |
| `users` | 5 | `user-001` … `user-005` | 3 STUDENT + 2 COMPANY, all BCrypt(`password`) |
| `internships` | 8 | `intern-001` … `intern-008` | Stipends 12000 – 20000, deadlines Oct–Dec 2026 |
| `skills` | 20 | `skill-001` … `skill-020` | 7 categories |
| `student_skills` | 15 | — | 3 skills per student |
| `internship_skills` | 22 | — | 2–4 skills per internship |
| `notifications` | 3 | `notif-001` … `notif-003` | 2 unread for `user-001`, 1 read for `user-003` |
| `applications` | 10 | `app-001` … `app-010` | All five statuses represented |

`schema.sql` starts with `DROP TABLE IF EXISTS` in reverse-dependency order
(`notifications`, `applications`, `internship_skills`, `student_skills`,
`internships`, `skills`, `companies`, `students`, `users`), so the script is
idempotent across restarts. This also means **restarting the application destroys all
local data** — acceptable for a demo, unacceptable in production. See
`SYSTEM_DESIGN.md` §11 and §14.

---

## 6. Indexing and performance notes

| Index | Type | Serves |
|---|---|---|
| PK on all 9 tables | clustered | Point lookups by id |
| `users.email` | unique | Login lookup, duplicate-account check |
| `students.email` | unique | Email lookup, duplicate-student check |
| `skills.skill_name` | unique | Name lookup, duplicate-skill check |
| `unique_application (student_id, internship_id)` | unique | Duplicate-application guard, per-student listing |
| Implicit FK indexes | InnoDB auto-created | `internships.company_id`, both junction tables, `applications.*`, `notifications.user_id` |

At the current demo scale (tens of rows) every table is a single-page scan, so no
additional index is justified. Once real usage begins, the first indexes to add
would be:

- `applications (status, created_at DESC)` — the dashboard's per-status counts.
- `internships (company_id, created_at DESC)` — the company drill-down listing.
- `applications (student_id, created_at DESC)` — already served by the FK index plus
  sort, so it is lower priority.

---

## 7. Data-access mapping

| Table | Model class | Row mapper | Key DAO methods |
|---|---|---|---|
| `users` | `User` | `UserDaoImpl.USER_ROW_MAPPER` | `findByEmail`, `findById`, `findAll`, `save`, `update`, `deleteById` |
| `students` | `Student` | `StudentDaoImpl.STUDENT_ROW_MAPPER` | `findAll`, `findById`, `findByEmail`, `save`, `update`, `deleteById` |
| `companies` | `Company` | `CompanyDaoImpl.COMPANY_ROW_MAPPER` | `findAll`, `findById`, `save`, `update`, `deleteById` |
| `internships` | `Internship` | `InternshipDaoImpl.INTERNSHIP_ROW_MAPPER` | `findAll`, `findById`, `findByCompanyId`, `save`, `update`, `deleteById` |
| `skills` | `Skill` | `SkillDaoImpl.SKILL_ROW_MAPPER` | `findAll`, `findById`, `findByName`, `save`, `deleteById` |
| `student_skills` | `StudentSkill` | `StudentSkillDaoImpl.STUDENT_SKILL_ROW_MAPPER` | `findByStudentId`, `findBySkillId`, `save`, `delete`, `deleteByStudentId` |
| `internship_skills` | `InternshipSkill` | `InternshipSkillDaoImpl.INTERNSHIP_SKILL_ROW_MAPPER` | `findByInternshipId`, `findBySkillId`, `save`, `delete`, `deleteByInternshipId` |
| `applications` | `InternshipApplication` | `ApplicationDaoImpl.APPLICATION_ROW_MAPPER` | `findAll`, `findById`, `findByStudentId`, `findByInternshipId`, `findByStatus`, `countByStatus`, `countAll`, `save`, `update`, `updateStatus`, `deleteById` |
| `notifications` | `Notification` | `NotificationDaoImpl.NOTIFICATION_ROW_MAPPER` | `findByUserId`, `findByUserIdAndIsRead`, `findById`, `save`, `updateIsRead`, `deleteById`, `deleteByUserId` |

**Type conversions applied by the mappers**

| DB type | Java type | Conversion |
|---|---|---|
| `TIMESTAMP` | `String` | `rs.getTimestamp(col).toString()` |
| `DATE` | `String` | `rs.getDate(col).toString()` |
| `DECIMAL(10,2)` | `double` | `rs.getDouble(col)` |
| `INT` | `int` | `rs.getInt(col)` |
| `BOOLEAN` | `boolean` | `rs.getBoolean(col)` |

A `NULL` timestamp or date yields the string `"null"` rather than a Java `null`,
because the converters are called unguarded. It is cosmetic in the current UI, but
it is a real sharp edge and is listed for cleanup.

**Return conventions**

- Single-row lookups return `null` when nothing matches — never an exception.
- The service layer converts `null` into `NotFoundException` (→ 404).
- Mutations return the affected row count from `jdbcTemplate.update(...)`; the
  services ignore it and re-read the row to return the persisted entity.
- Counts use `jdbcTemplate.queryForObject(sql, Long.class, ...)` and are exposed as
  `Long`.
