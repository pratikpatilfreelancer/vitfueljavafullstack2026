-- ---------------------------------------------------------------------------
-- Expense Voucher Management System (EVMS)
-- SQLite Database Schema
-- ---------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS users (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  name          TEXT NOT NULL,
  email         TEXT NOT NULL UNIQUE,
  password_hash TEXT NOT NULL,
  role          TEXT NOT NULL CHECK (role IN ('EMPLOYEE', 'DIRECTOR', 'ACCOUNTS')),
  department    TEXT,
  created_at    TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS vouchers (
  id                 INTEGER PRIMARY KEY AUTOINCREMENT,
  voucher_number     TEXT NOT NULL UNIQUE,
  employee_id        INTEGER NOT NULL REFERENCES users(id),
  employee_name      TEXT NOT NULL,
  employee_code      TEXT,
  department         TEXT NOT NULL,
  voucher_date       TEXT NOT NULL,
  expense_date       TEXT NOT NULL,
  expense_title      TEXT NOT NULL,
  expense_category   TEXT NOT NULL,
  expense_description TEXT,
  amount             REAL NOT NULL CHECK (amount > 0),
  status             TEXT NOT NULL DEFAULT 'DRAFT'
                        CHECK (status IN ('DRAFT','SUBMITTED','APPROVED','REJECTED')),
  employee_signature TEXT,
  director_signature TEXT,
  proof_document     TEXT,
  approval_date      TEXT,
  rejection_reason   TEXT,
  approved_by        INTEGER REFERENCES users(id),
  created_at         TEXT NOT NULL DEFAULT (datetime('now')),
  updated_at         TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE INDEX IF NOT EXISTS idx_vouchers_employee ON vouchers(employee_id);
CREATE INDEX IF NOT EXISTS idx_vouchers_status ON vouchers(status);

CREATE TABLE IF NOT EXISTS voucher_counter (
  year INTEGER PRIMARY KEY,
  seq  INTEGER NOT NULL
);

-- Keep updated_at fresh automatically.
CREATE TRIGGER IF NOT EXISTS trg_vouchers_updated_at
AFTER UPDATE ON vouchers
FOR EACH ROW
BEGIN
  UPDATE vouchers SET updated_at = datetime('now') WHERE id = OLD.id;
END;
