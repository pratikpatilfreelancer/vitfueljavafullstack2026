-- ============================================================
-- SmartHire - Resume Screening & Recruitment Management System
-- Database schema (MySQL 8.0+)
-- ============================================================

DROP DATABASE IF EXISTS smarthire;
CREATE DATABASE smarthire CHARACTER SET utf8mb4;
USE smarthire;

-- ------------------------------------------------------------
-- users: authentication + shared identity for candidates/recruiters
-- ------------------------------------------------------------
CREATE TABLE users (
    user_id      INT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(50)  NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,          -- SHA-256 hash
    role         ENUM('CANDIDATE', 'RECRUITER') NOT NULL,
    email        VARCHAR(100) NOT NULL UNIQUE,
    full_name    VARCHAR(100) NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- candidates: profile data used by the resume screening engine
-- ------------------------------------------------------------
CREATE TABLE candidates (
    candidate_id     INT AUTO_INCREMENT PRIMARY KEY,
    user_id          INT NOT NULL,
    phone            VARCHAR(20),
    skills           VARCHAR(500),   -- comma separated skill keywords
    education        VARCHAR(255),
    experience_years INT DEFAULT 0,
    resume_text      TEXT,           -- plain-text resume content, used for keyword matching
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- recruiters: profile data for recruiter accounts
-- ------------------------------------------------------------
CREATE TABLE recruiters (
    recruiter_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id      INT NOT NULL,
    company_name VARCHAR(150),
    department   VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- jobs: postings created by recruiters
-- ------------------------------------------------------------
CREATE TABLE jobs (
    job_id           INT AUTO_INCREMENT PRIMARY KEY,
    recruiter_id     INT NOT NULL,
    title            VARCHAR(150) NOT NULL,
    description      TEXT,
    required_skills  VARCHAR(500),  -- comma separated skill keywords
    min_experience   INT DEFAULT 0,
    location         VARCHAR(150),
    status           ENUM('OPEN', 'CLOSED') DEFAULT 'OPEN',
    posted_date      DATE NOT NULL,
    FOREIGN KEY (recruiter_id) REFERENCES recruiters(recruiter_id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- applications: a candidate applying to a job, with a computed match_score
-- ------------------------------------------------------------
CREATE TABLE applications (
    application_id INT AUTO_INCREMENT PRIMARY KEY,
    job_id         INT NOT NULL,
    candidate_id   INT NOT NULL,
    applied_date   DATE NOT NULL,
    status         ENUM('APPLIED', 'SHORTLISTED', 'REJECTED', 'INTERVIEW_SCHEDULED', 'HIRED')
                       DEFAULT 'APPLIED',
    match_score    DECIMAL(5,2) DEFAULT 0.00,   -- 0.00 - 100.00, set by ResumeScreeningService
    FOREIGN KEY (job_id) REFERENCES jobs(job_id) ON DELETE CASCADE,
    FOREIGN KEY (candidate_id) REFERENCES candidates(candidate_id) ON DELETE CASCADE,
    UNIQUE KEY uq_job_candidate (job_id, candidate_id)  -- one application per candidate per job
);

-- ------------------------------------------------------------
-- interviews: scheduling + feedback tied to a single application
-- ------------------------------------------------------------
CREATE TABLE interviews (
    interview_id    INT AUTO_INCREMENT PRIMARY KEY,
    application_id  INT NOT NULL,
    interview_date  DATE NOT NULL,
    interview_time  TIME NOT NULL,
    mode            ENUM('ONLINE', 'IN_PERSON', 'PHONE') DEFAULT 'ONLINE',
    status          ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED') DEFAULT 'SCHEDULED',
    feedback        TEXT,
    rating          INT DEFAULT 0,   -- 1-5, 0 = not yet rated
    FOREIGN KEY (application_id) REFERENCES applications(application_id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Helpful indexes for common lookups
-- ------------------------------------------------------------
CREATE INDEX idx_jobs_status ON jobs(status);
CREATE INDEX idx_applications_status ON applications(status);
CREATE INDEX idx_applications_candidate ON applications(candidate_id);
CREATE INDEX idx_applications_job ON applications(job_id);
CREATE INDEX idx_interviews_date ON interviews(interview_date);
