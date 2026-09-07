# SmartHire — Resume Screening & Recruitment Management System

A desktop recruitment management application built with **Core Java + Swing
(UI) + JDBC + MySQL**. Designed as a college capstone project: professional
in structure, realistic in scope.

## What it does

SmartHire supports two roles:

- **Candidates** can register, build a profile (skills, education,
  experience, resume text), search/browse open jobs, apply, and track the
  status and match score of every application.
- **Recruiters** can register, post and manage job openings, view and rank
  applicants, run rule-based resume screening, shortlist/reject/hire
  candidates, schedule interviews and record feedback, and view basic
  recruitment reports (applications by status, average match score per job,
  etc.).

### How "resume screening" works

`service/ResumeScreeningService.java` implements a transparent, rule-based
match score out of 100:

- **70%** — the percentage of a job's required skills found in the
  candidate's skills list / resume text (simple keyword matching).
- **30%** — how the candidate's years of experience compare to the job's
  minimum required experience.

This is intentionally simple and explainable rather than a black-box ML
model — a good fit for a capstone project, and easy to extend later (e.g.
weighting particular skills, adding synonyms, or plugging in a real NLP
library) if you want to take it further.

## Project structure

```
SmartHire/
├── src/
│   ├── model/       -- POJOs: User, Candidate, Recruiter, Job, Application, Interview
│   ├── dao/         -- JDBC data access objects (one per table) + DBConnection
│   ├── service/      -- business logic: ResumeScreeningService, ApplicationService, InterviewService
│   ├── ui/          -- Swing screens (LoginFrame, RegisterFrame, ui/candidate/*, ui/recruiter/*)
│   ├── util/        -- ValidationUtil, SessionManager
│   └── Main.java    -- application entry point
├── database/
│   ├── schema.sql       -- creates the `smarthire` database and all tables
│   ├── sample_data.sql  -- demo users, jobs, applications, an interview
│   └── queries.sql      -- reference SELECT queries for demos/debugging
├── lib/
│   └── (place mysql-connector-j.jar here — see lib/README_DOWNLOAD_DRIVER.txt)
├── resources/
│   └── images/      -- optional icons/logo, not required to run
└── SmartHire.iml    -- IntelliJ module file (pre-wired to lib/mysql-connector-j.jar)
```

## Setup

### 1. Prerequisites
- JDK 8 or later
- MySQL 8.0+ server running locally (or reachable over the network)
- MySQL Connector/J JDBC driver — **not bundled** in this zip because the
  build environment used to generate this project has no internet access.
  See `lib/README_DOWNLOAD_DRIVER.txt` for exact download/setup steps.

### 2. Create the database
In a MySQL client (Workbench, `mysql` CLI, etc.):
```sql
SOURCE database/schema.sql;
SOURCE database/sample_data.sql;   -- optional, but recommended for a first run
```

### 3. Configure the connection
Edit `src/dao/DBConnection.java` and update:
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/smarthire?useSSL=false&serverTimezone=UTC";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "root";
```
to match your local MySQL username/password.

### 4. Build & run

**IntelliJ IDEA**: Open the `SmartHire` folder as a project (the `.iml`
file already points at `lib/mysql-connector-j.jar` once you've added the
driver there). Run `Main.java`.

**Eclipse**: Import as an existing Java project, add
`lib/mysql-connector-j.jar` to the build path, run `Main.java`.

**Command line**:
```bash
# compile
javac -cp "lib/mysql-connector-j.jar" -d out $(find src -name "*.java")

# run (use ; instead of : on Windows)
java -cp "out:lib/mysql-connector-j.jar" Main
```

## Demo accounts (after loading `sample_data.sql`)

All demo passwords are `password123`.

| Username     | Role      |
|--------------|-----------|
| recruiter1   | RECRUITER |
| recruiter2   | RECRUITER |
| candidate1   | CANDIDATE |
| candidate2   | CANDIDATE |
| candidate3   | CANDIDATE |
| candidate4   | CANDIDATE |

Or click "Create Account" on the login screen to register a fresh account
of either role.

## Notes / possible extensions

- Passwords are stored as SHA-256 hashes (`UserDAO.hashPassword`), not
  plaintext — but for a production system you'd want a salted, slow hash
  (e.g. bcrypt) instead.
- The resume "parsing" is plain text the candidate pastes in — there's no
  file upload/PDF parsing, keeping the dependency list at zero beyond the
  JDBC driver. Adding a PDF-to-text step (e.g. Apache PDFBox) would be a
  natural next step.
- `RecruitmentReports` shows numbers as plain labels rather than charts,
  again to avoid extra dependencies — swapping in JFreeChart would be a
  nice enhancement if allowed by your course/project rules.
