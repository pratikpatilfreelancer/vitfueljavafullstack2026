# Expense Voucher Management System (EVMS)

A full-stack web app that digitizes ABC Company's expense voucher lifecycle:
**Draft → Submitted → Approved / Rejected**, with three roles (Employee, Director,
Accounts Team) enforced end-to-end.

Built for the Prachay Securities Full Stack Developer Internship assignment.

- **Frontend:** React 19 (Vite), React Router, Axios — custom "ledger & paper voucher" design system, no UI framework.
- **Backend:** Node.js, Express, JWT auth, Multer (signature uploads), bcrypt.
- **Database:** SQLite via `better-sqlite3` (file-based — zero setup, no external DB service required to run this).
  The schema and query layer use plain SQL, so porting to PostgreSQL/MySQL only means swapping the driver in `db/db.js`.

---

## 1. Project Setup

### Prerequisites
- Node.js 18+
- npm

### Backend

```bash
cd backend
npm install
cp .env.example .env        # defaults work out of the box
npm run seed                # creates evms.sqlite3 + 3 demo users
npm start                   # http://localhost:4000
```

### Frontend

```bash
cd frontend
npm install
cp .env.example .env        # points VITE_API_URL at http://localhost:4000/api
npm run dev                 # http://localhost:5173
```

### Demo accounts (password for all: `password123`)

| Role      | Email                  |
|-----------|-------------------------|
| Employee  | `employee@evms.test`   |
| Director  | `director@evms.test`   |
| Accounts  | `accounts@evms.test`   |

Log in as Employee → create a voucher → attach a signature and submit → log in as
Director → approve/reject → log in as Accounts → see it appear in the ledger.

---

## 2. Database Schema

**`users`**
| Column | Type | Notes |
|---|---|---|
| id | INTEGER PK | |
| name, email | TEXT | email is unique |
| password_hash | TEXT | bcrypt |
| role | TEXT | `EMPLOYEE` \| `DIRECTOR` \| `ACCOUNTS` |
| department | TEXT | |

**`vouchers`**
| Column | Type | Notes |
|---|---|---|
| id | INTEGER PK | |
| voucher_number | TEXT | unique, auto-generated `EV-{year}-{seq}` |
| employee_id | INTEGER FK → users.id | owner |
| employee_name, employee_code | TEXT | denormalized snapshot at creation time |
| department, voucher_date, expense_date, expense_title, expense_category, expense_description | TEXT | |
| amount | REAL | `CHECK (amount > 0)` |
| status | TEXT | `DRAFT` \| `SUBMITTED` \| `APPROVED` \| `REJECTED` |
| employee_signature, director_signature | TEXT | stored filenames under `/uploads/signatures` |
| approval_date, rejection_reason, approved_by | | set on approve/reject |
| created_at, updated_at | TEXT | `updated_at` auto-maintained by a trigger |

**`voucher_counter`** — one row per year, tracks the running sequence used to mint
`voucher_number`s atomically (avoids collisions from concurrent submissions).

Schema lives in `backend/db/db.js` and is created automatically on first run
(`CREATE TABLE IF NOT EXISTS`) — no separate migration step needed for SQLite.
Uploaded signatures are served statically from `/uploads/signatures/<file>`.

---

## 3. API Documentation

Base URL: `http://localhost:4000/api`. All routes except `/auth/login` require
`Authorization: Bearer <token>`.

### Auth
| Method | Route | Role | Body | Notes |
|---|---|---|---|---|
| POST | `/auth/login` | — | `{ email, password }` | returns `{ token, user }` |
| GET | `/auth/me` | any | — | returns the decoded token payload |

### Vouchers
| Method | Route | Role | Notes |
|---|---|---|---|
| POST | `/vouchers` | Employee | creates a `DRAFT` voucher, auto-assigns voucher number |
| GET | `/vouchers` | any | Employees see only their own; Director/Accounts see all. Supports query params: `voucherNumber, employeeName, department, category, status, dateFrom, dateTo, amountMin, amountMax, sortBy, sortDir` |
| GET | `/vouchers/dashboard/summary` | any | role-specific KPI payload (see below) |
| GET | `/vouchers/:id` | any | 403 if an Employee requests someone else's voucher |
| PUT | `/vouchers/:id` | Employee (owner) | only while `status = DRAFT` |
| DELETE | `/vouchers/:id` | Employee (owner) | only while `status = DRAFT` |
| POST | `/vouchers/:id/submit` | Employee (owner) | multipart form, field `signature` (image); moves `DRAFT → SUBMITTED` |
| POST | `/vouchers/:id/approve` | Director | multipart form, field `signature` (image, required); moves `SUBMITTED → APPROVED` |
| POST | `/vouchers/:id/reject` | Director | JSON `{ rejectionReason }` (required); moves `SUBMITTED → REJECTED` |

**Dashboard summary shape** differs by the caller's role:
- Employee: `totalVouchers, draftVouchers, pendingApproval, approvedVouchers, rejectedVouchers, totalAmountClaimed`
- Director: `pendingApprovalCount, approvedToday, rejectedToday, totalPendingAmount, recentActivity[]`
- Accounts: `totalVouchers, pendingApproval, approvedVouchers, rejectedVouchers, totalApprovedExpenseAmount, recentApprovedVouchers[]`

All error responses are `{ "error": "human readable message" }` with an appropriate
HTTP status (400 validation, 401 auth, 403 authorization, 404 not found, 409 invalid
state transition).

---

## 4. Business Rules Implemented

- Voucher numbers are unique and auto-generated (`EV-<year>-<sequence>`), backed by a
  dedicated counter table so numbering never collides.
- Every new voucher starts as `DRAFT`.
- Employees can edit/delete only their own `DRAFT` vouchers; once `SUBMITTED` a
  voucher is read-only to them.
- Only a Director can approve/reject, and only vouchers currently `SUBMITTED`.
- Approving requires a Director signature image; rejecting requires a non-empty
  rejection reason — both enforced server-side, not just in the UI.
- Approved vouchers are terminal (read-only); rejected vouchers carry the reason.
- Employees only ever see their own vouchers (enforced in the SQL `WHERE` clause,
  not just hidden in the UI); Director and Accounts see everything.
- Search/filter/sort (bonus requirement) is implemented server-side across voucher
  number, employee, department, category, status, date range, and amount range, plus
  sortable columns in the ledger table headers.

## 5. Assumptions Made

- **Database:** SQLite was used in place of PostgreSQL/MySQL to keep setup to
  `npm install && npm run seed && npm start` with no external service — the SQL is
  plain enough to port directly if a client/server DB is required.
- **User provisioning:** the assignment doesn't specify a signup flow, and a
  real company would provision Employee/Director/Accounts accounts internally, so
  there's no public registration screen — accounts are seeded via `db/seed.js`.
  In a production system this would instead be an admin-only "Create User" screen.
- **Employee ID** is treated as optional free text, per the spec ("Employee ID
  (Optional)").
- **Voucher editing** is restricted to the fields listed in section 5 of the
  spec; voucher number, employee identity, and audit timestamps are never
  user-editable.
- **Signature storage:** signature images are stored on local disk under
  `backend/uploads/signatures` and served statically; in production these would
  more likely live in S3/Blob storage.
- **"Download or print vouchers" (optional, Accounts)** was treated as satisfied by
  the browser's native print-to-PDF on the voucher detail page, rather than a
  bespoke PDF export endpoint, to keep scope focused on the required workflow.

## 6. Project Structure

```
evms/
├── backend/
│   ├── db/            # SQLite connection, schema, seed script
│   ├── middleware/     # JWT auth + role guard
│   ├── routes/         # auth.js, vouchers.js
│   ├── uploads/         # uploaded signature images (gitignored contents)
│   ├── server.js
│   └── .env.example
└── frontend/
    ├── src/
    │   ├── api/         # axios client
    │   ├── components/  # Layout, ProtectedRoute, VoucherTable, FiltersBar, StatusStamp
    │   ├── context/      # AuthContext
    │   ├── pages/        # Login, employee/, director/, accounts/, shared VoucherDetails
    │   └── styles.css
    └── .env.example
```
