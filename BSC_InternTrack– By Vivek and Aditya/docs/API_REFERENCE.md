# InternTrack — API Reference

**Base URL:** `http://localhost:8080/api`
**Media type:** `application/json`
**Live documentation:** `http://localhost:8080/swagger-ui.html` (OpenAPI JSON at `/api-docs`)
**Authentication:** none in this version — see `SYSTEM_DESIGN.md` §8

---

## Conventions

| Aspect | Rule |
|---|---|
| Success — read | `200 OK` + JSON entity or array |
| Success — create | `201 Created` + the created entity |
| Success — delete | `204 No Content`, empty body |
| Error | `{ "status": <int>, "message": "<string>", "timestamp": "<ISO-8601>" }` |
| Validation | `@Valid` on request bodies for all entity endpoints except the two skill-mapping controllers |
| Naming | plural resources; drill-downs as path segments; actions as sub-resources (`/{id}/status`, `/{id}/read`); aggregates namespaced under `/stats` |
| CORS | Allowed from `http://localhost:8080` and `http://127.0.0.1:8080` only |

**Standard statuses**

| Code | Trigger |
|---|---|
| `400 Bad Request` | `IllegalArgumentException` — missing required field, invalid status value |
| `404 Not Found` | `NotFoundException` — entity, or a referenced parent, does not exist |
| `409 Conflict` | `DuplicateResourceException` — duplicate email or skill name |
| `500 Internal Server Error` | any other unhandled exception |

---

## 1. Authentication — `/api/auth`

`AuthController` · Swagger tag **Authentication**

### `POST /api/auth/login`

Authenticate with email and password.

**Request body**

```json
{ "email": "alice@example.com", "password": "password" }
```

**`200 OK`**

```json
{
  "userId": "user-001",
  "email": "alice@example.com",
  "role": "STUDENT",
  "refId": "student-001",
  "message": "Login successful"
}
```

**Errors** — `400 Invalid email or password` for both an unknown email and a wrong
password (deliberately identical, to avoid account enumeration).

> The response carries identity only. No token is issued; the browser stores this
> object under `localStorage` key `interntrack_user`.

### `POST /api/auth/register`

Create a login account.

**Request body**

| Field | Type | Required | Notes |
|---|---|---|---|
| `email` | string | yes | must be unique |
| `password` | string | yes | stored BCrypt-hashed, never returned |
| `role` | string | yes | `STUDENT` or `COMPANY` |
| `refId` | string | no | `student_id` or `company_id` this account represents |
| `userId` | string | no | ignored — the server generates `user-<8 hex>` |

**`201 Created`** — same body shape as login, with `"Registration successful"`.

**Errors** — `400` if `email`, `password` or `role` is blank · `409` if the email is
already registered.

---

## 2. Students — `/api/students`

`StudentController` · Swagger tag **Students**

| Method | Path | Description | Success |
|---|---|---|---|
| `GET` | `/api/students` | List all students, newest first | `200` array |
| `GET` | `/api/students/{id}` | Fetch one student | `200` object |
| `GET` | `/api/students/email/{email}` | Fetch one student by email | `200` object |
| `POST` | `/api/students` | Create a student | `201` object |
| `PUT` | `/api/students/{id}` | Update a student (path id wins) | `200` object |
| `DELETE` | `/api/students/{id}` | Delete a student and, by cascade, their applications and skill links | `204` |

**Model**

```json
{
  "studentId": "student-001",
  "name": "Alice Johnson",
  "email": "alice@example.com",
  "phone": "9876543210",
  "college": "ABC College",
  "course": "Computer Science",
  "year": 3,
  "resumeUrl": "https://example.com/resumes/alice.pdf",
  "createdAt": "2026-09-30T16:20:11.482"
}
```

**Rules** — `studentId`, `name` and `email` are required (`400` otherwise) · a
duplicate `email` returns `409` · a duplicate `studentId` returns `409` · an unknown
id returns `404`.

> The Java model also has a `skills` field, but it is not persisted and is not
> returned. Use `/api/student-skills/student/{studentId}` to read a student's skills.

---

## 3. Companies — `/api/companies`

`CompanyController` · Swagger tag **Companies**

| Method | Path | Description | Success |
|---|---|---|---|
| `GET` | `/api/companies` | List all companies, newest first | `200` array |
| `GET` | `/api/companies/{id}` | Fetch one company | `200` object |
| `POST` | `/api/companies` | Create a company | `201` object |
| `PUT` | `/api/companies/{id}` | Update a company (path id wins) | `200` object |
| `DELETE` | `/api/companies/{id}` | Delete a company; by cascade this also deletes its internships, their skill links, and any applications to them | `204` |

**Model**

```json
{
  "companyId": "company-001",
  "companyName": "TechCorp Solutions",
  "location": "Bangalore",
  "industry": "Software",
  "website": "https://techcorp.com",
  "contactEmail": "hr@techcorp.com",
  "createdAt": "2026-09-30T16:20:11.482"
}
```

**Rules** — `companyId` and `companyName` are required (`400`) · a duplicate
`companyId` returns `400` (`IllegalArgumentException` in this implementation) · an
unknown id returns `404`.

---

## 4. Internships — `/api/internships`

`InternshipController` · Swagger tag **Internships**

| Method | Path | Description | Success |
|---|---|---|---|
| `GET` | `/api/internships` | List all internships, newest first | `200` array |
| `GET` | `/api/internships/{id}` | Fetch one internship | `200` object |
| `GET` | `/api/internships/company/{companyId}` | List a company's internships, newest first (empty array if the company has none) | `200` array |
| `POST` | `/api/internships` | Create an internship | `201` object |
| `PUT` | `/api/internships/{id}` | Update an internship (path id wins) | `200` object |
| `DELETE` | `/api/internships/{id}` | Delete an internship; cascades to its skill links and applications | `204` |

**Model**

```json
{
  "internshipId": "intern-001",
  "companyId": "company-001",
  "title": "Software Developer Intern",
  "description": "Develop web applications using Spring Boot",
  "location": "Bangalore",
  "duration": "3 Months",
  "stipend": 15000.00,
  "applicationDeadline": "2026-10-30",
  "createdAt": "2026-09-30T16:20:11.482"
}
```

**Rules** — `internshipId`, `title` and `companyId` are required (`400`) · the
referenced company must exist, else `404` · a duplicate `internshipId` returns `400`
· an unknown internship id returns `404`.

> The Java model also has a `requiredSkills` field, but it is not persisted. Use
> `/api/internship-skills/internship/{internshipId}` to manage required skills.

---

## 5. Skills — `/api/skills`

`SkillController` · Swagger tag **Skills**

| Method | Path | Description | Success |
|---|---|---|---|
| `GET` | `/api/skills` | List all skills, alphabetically | `200` array |
| `GET` | `/api/skills/{id}` | Fetch one skill by id | `200` object |
| `GET` | `/api/skills/name/{name}` | Fetch one skill by name | `200` object |
| `POST` | `/api/skills` | Create a skill | `201` object |
| `DELETE` | `/api/skills/{id}` | Delete a skill; cascades from both junction tables | `204` |

**Model**

```json
{
  "skillId": "skill-001",
  "skillName": "Java",
  "category": "Programming",
  "createdAt": "2026-09-30T16:20:11.482"
}
```

**Rules** — `skillName` is required (`400`) · a duplicate `skillName` returns `409` ·
an unknown id or name returns `404`. There is no `PUT` endpoint — a skill is either
created or removed.

---

## 6. Student ↔ Skill mapping — `/api/student-skills`

`StudentSkillController` · Swagger tag **Student Skills**

| Method | Path | Body | Description | Success |
|---|---|---|---|---|
| `GET` | `/api/student-skills/student/{studentId}` | — | All skill links for a student, oldest first | `200` array |
| `GET` | `/api/student-skills/skill/{skillId}` | — | All student links for a skill | `200` array |
| `POST` | `/api/student-skills` | `{"studentId":"student-001","skillId":"skill-005"}` | Attach a skill to a student | `201`, empty body |
| `DELETE` | `/api/student-skills` | `{"studentId":"student-001","skillId":"skill-005"}` | Detach one skill | `204`, empty body |
| `DELETE` | `/api/student-skills/student/{studentId}` | — | Detach every skill from a student | `204`, empty body |

**Link model**

```json
{ "studentId": "student-001", "skillId": "skill-005", "createdAt": "2026-09-30T16:20:11.482" }
```

**Rules** — both the student and the skill must exist, else `404` · re-attaching an
existing pair is a **no-op success**, not an error (the service skips the insert) ·
this endpoint group does not apply `@Valid`.

---

## 7. Internship ↔ Skill mapping — `/api/internship-skills`

`InternshipSkillController` · Swagger tag **Internship Skills**

| Method | Path | Body | Description | Success |
|---|---|---|---|---|
| `GET` | `/api/internship-skills/internship/{internshipId}` | — | All skill links for an internship, oldest first | `200` array |
| `GET` | `/api/internship-skills/skill/{skillId}` | — | All internship links for a skill | `200` array |
| `POST` | `/api/internship-skills` | `{"internshipId":"intern-001","skillId":"skill-005"}` | Attach a required skill to an internship | `201`, empty body |
| `DELETE` | `/api/internship-skills` | `{"internshipId":"intern-001","skillId":"skill-005"}` | Detach one required skill | `204`, empty body |
| `DELETE` | `/api/internship-skills/internship/{internshipId}` | — | Detach every required skill from an internship | `204`, empty body |

Rules are identical to §6, with `internshipId` in place of `studentId`.

---

## 8. Applications — `/api/applications`

`ApplicationController` · Swagger tag **Applications**

### Read

| Method | Path | Description | Success |
|---|---|---|---|
| `GET` | `/api/applications` | All applications, newest first | `200` array |
| `GET` | `/api/applications/{id}` | One application | `200` object |
| `GET` | `/api/applications/student/{studentId}` | Applications of one student, newest first | `200` array |
| `GET` | `/api/applications/internship/{internshipId}` | Applications for one internship, newest first | `200` array |
| `GET` | `/api/applications/status/{status}` | Applications in one status, newest first | `200` array |

`/status/{status}` validates against `Applied`, `Shortlisted`, `Interview`,
`Selected`, `Rejected` and returns `400` for anything else.

### Statistics

| Method | Path | Response |
|---|---|---|
| `GET` | `/api/applications/stats/all` | `{ "total": 10 }` |
| `GET` | `/api/applications/stats/status/{status}` | `{ "count": 2 }` |

These six cards are exactly what `dashboard.js` loads in parallel on page load.

### Write

| Method | Path | Description | Success |
|---|---|---|---|
| `POST` | `/api/applications` | Submit an application | `201` object |
| `PUT` | `/api/applications/{id}` | Update an application (path id wins) | `200` object |
| `PUT` | `/api/applications/{id}/status?status={status}` | Advance an application and notify the student | `200` object |
| `DELETE` | `/api/applications/{id}` | Withdraw/delete an application | `204` |

**Model**

```json
{
  "applicationId": "app-001",
  "studentId": "student-001",
  "internshipId": "intern-001",
  "applicationDate": "2026-09-01",
  "status": "Shortlisted",
  "notes": "Good resume",
  "createdAt": "2026-09-30T16:20:11.482",
  "updatedAt": "2026-09-30T16:20:11.482"
}
```

**Rules**

- `applicationId`, `studentId` and `internshipId` are required (`400`).
- The student and the internship must both exist, else `404`.
- A blank `status` on create defaults to `Applied`; a supplied status must be one of
  the five allowed values, else `400`.
- A duplicate `applicationId` returns `400`.
- A second application by the same student to the same internship is rejected by the
  `UNIQUE (student_id, internship_id)` constraint and surfaces as `500`.
- A status change additionally writes a `STATUS_UPDATE` notification for the
  student's user. Notification failures are logged and never fail the request.

---

## 9. Notifications — `/api/notifications`

`NotificationController` · Swagger tag **Notifications**

| Method | Path | Description | Success |
|---|---|---|---|
| `GET` | `/api/notifications/user/{userId}` | All notifications for a user, newest first | `200` array |
| `GET` | `/api/notifications/user/{userId}/unread` | Unread notifications only | `200` array |
| `GET` | `/api/notifications/{id}` | One notification | `200` object |
| `POST` | `/api/notifications` | Create a notification | `201` object |
| `PUT` | `/api/notifications/{id}/read` | Mark one as read | `200` object |
| `DELETE` | `/api/notifications/{id}` | Delete one notification | `204` |
| `DELETE` | `/api/notifications/user/{userId}` | Delete all notifications for a user | `204` |

**Model**

```json
{
  "notificationId": "notif-001",
  "userId": "user-001",
  "message": "Your application for Software Developer Intern has been Shortlisted",
  "type": "STATUS_UPDATE",
  "isRead": false,
  "createdAt": "2026-09-30T16:20:11.482"
}
```

**Rules** — `userId` and `message` are required (`400`) · an unknown id returns `404`.
"Mark all as read" in the UI is not a server endpoint: `notifications.js` issues one
`PUT /{id}/read` per unread notification.

---

## 10. Endpoint index

| # | Method | Path | Controller |
|---|---|---|---|
| 1 | `POST` | `/api/auth/login` | `AuthController` |
| 2 | `POST` | `/api/auth/register` | `AuthController` |
| 3 | `GET` | `/api/students` | `StudentController` |
| 4 | `GET` | `/api/students/{id}` | `StudentController` |
| 5 | `GET` | `/api/students/email/{email}` | `StudentController` |
| 6 | `POST` | `/api/students` | `StudentController` |
| 7 | `PUT` | `/api/students/{id}` | `StudentController` |
| 8 | `DELETE` | `/api/students/{id}` | `StudentController` |
| 9 | `GET` | `/api/companies` | `CompanyController` |
| 10 | `GET` | `/api/companies/{id}` | `CompanyController` |
| 11 | `POST` | `/api/companies` | `CompanyController` |
| 12 | `PUT` | `/api/companies/{id}` | `CompanyController` |
| 13 | `DELETE` | `/api/companies/{id}` | `CompanyController` |
| 14 | `GET` | `/api/internships` | `InternshipController` |
| 15 | `GET` | `/api/internships/{id}` | `InternshipController` |
| 16 | `GET` | `/api/internships/company/{companyId}` | `InternshipController` |
| 17 | `POST` | `/api/internships` | `InternshipController` |
| 18 | `PUT` | `/api/internships/{id}` | `InternshipController` |
| 19 | `DELETE` | `/api/internships/{id}` | `InternshipController` |
| 20 | `GET` | `/api/skills` | `SkillController` |
| 21 | `GET` | `/api/skills/{id}` | `SkillController` |
| 22 | `GET` | `/api/skills/name/{name}` | `SkillController` |
| 23 | `POST` | `/api/skills` | `SkillController` |
| 24 | `DELETE` | `/api/skills/{id}` | `SkillController` |
| 25 | `GET` | `/api/student-skills/student/{studentId}` | `StudentSkillController` |
| 26 | `GET` | `/api/student-skills/skill/{skillId}` | `StudentSkillController` |
| 27 | `POST` | `/api/student-skills` | `StudentSkillController` |
| 28 | `DELETE` | `/api/student-skills` | `StudentSkillController` |
| 29 | `DELETE` | `/api/student-skills/student/{studentId}` | `StudentSkillController` |
| 30 | `GET` | `/api/internship-skills/internship/{internshipId}` | `InternshipSkillController` |
| 31 | `GET` | `/api/internship-skills/skill/{skillId}` | `InternshipSkillController` |
| 32 | `POST` | `/api/internship-skills` | `InternshipSkillController` |
| 33 | `DELETE` | `/api/internship-skills` | `InternshipSkillController` |
| 34 | `DELETE` | `/api/internship-skills/internship/{internshipId}` | `InternshipSkillController` |
| 35 | `GET` | `/api/applications` | `ApplicationController` |
| 36 | `GET` | `/api/applications/{id}` | `ApplicationController` |
| 37 | `GET` | `/api/applications/student/{studentId}` | `ApplicationController` |
| 38 | `GET` | `/api/applications/internship/{internshipId}` | `ApplicationController` |
| 39 | `GET` | `/api/applications/status/{status}` | `ApplicationController` |
| 40 | `GET` | `/api/applications/stats/all` | `ApplicationController` |
| 41 | `GET` | `/api/applications/stats/status/{status}` | `ApplicationController` |
| 42 | `POST` | `/api/applications` | `ApplicationController` |
| 43 | `PUT` | `/api/applications/{id}` | `ApplicationController` |
| 44 | `PUT` | `/api/applications/{id}/status` | `ApplicationController` |
| 45 | `DELETE` | `/api/applications/{id}` | `ApplicationController` |
| 46 | `GET` | `/api/notifications/user/{userId}` | `NotificationController` |
| 47 | `GET` | `/api/notifications/user/{userId}/unread` | `NotificationController` |
| 48 | `GET` | `/api/notifications/{id}` | `NotificationController` |
| 49 | `POST` | `/api/notifications` | `NotificationController` |
| 50 | `PUT` | `/api/notifications/{id}/read` | `NotificationController` |
| 51 | `DELETE` | `/api/notifications/{id}` | `NotificationController` |
| 52 | `DELETE` | `/api/notifications/user/{userId}` | `NotificationController` |

**52 endpoints across 9 controllers.** `UserService` is deliberately not exposed as a
REST resource — it is reachable only through registration and login.

---

## 11. Worked example — end to end

```bash
# 1. Log in as a seeded student
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","password":"password"}'
# → 200 { "userId": "user-001", "email": "alice@example.com",
#          "role": "STUDENT", "refId": "student-001", ... }

# 2. Read Alice's applications
curl http://localhost:8080/api/applications/student/student-001

# 3. Advance one to Selected (writes a notification for user-001)
curl -X PUT "http://localhost:8080/api/applications/app-001/status?status=Selected"

# 4. Read the resulting notification
curl http://localhost:8080/api/notifications/user/user-001/unread

# 5. Dashboard counters
curl http://localhost:8080/api/applications/stats/all          # { "total": 10 }
curl http://localhost:8080/api/applications/stats/status/Selected
```
