CREATE DATABASE IF NOT EXISTS resume_screening;
USE resume_screening;

CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role ENUM('CANDIDATE','RECRUITER') NOT NULL
);

CREATE TABLE IF NOT EXISTS candidates (
    candidate_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(30),
    skills TEXT,
    resume_text LONGTEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS recruiters (
    recruiter_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    company_name VARCHAR(150) NOT NULL,
    email VARCHAR(100) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS jobs (
    job_id INT PRIMARY KEY AUTO_INCREMENT,
    recruiter_id INT NOT NULL,
    job_title VARCHAR(150) NOT NULL,
    description TEXT,
    required_skills TEXT NOT NULL,
    FOREIGN KEY (recruiter_id) REFERENCES recruiters(recruiter_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS applications (
    application_id INT PRIMARY KEY AUTO_INCREMENT,
    candidate_id INT NOT NULL,
    job_id INT NOT NULL,
    screening_score DECIMAL(5,2) DEFAULT 0,
    status ENUM('APPLIED','SHORTLISTED','REJECTED') DEFAULT 'APPLIED',
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(candidate_id, job_id),
    FOREIGN KEY (candidate_id) REFERENCES candidates(candidate_id) ON DELETE CASCADE,
    FOREIGN KEY (job_id) REFERENCES jobs(job_id) ON DELETE CASCADE
);

-- Insert base users into parent table
INSERT IGNORE INTO users(username, password, role) VALUES ('candidate', 'candidate123', 'CANDIDATE');
INSERT IGNORE INTO users(username, password, role) VALUES ('recruiter', 'recruiter123', 'RECRUITER');

-- Insert candidate using the user_id corresponding to 'candidate'
INSERT IGNORE INTO candidates(user_id, name, email, phone, skills, resume_text)
SELECT user_id, 'Demo Candidate', 'candidate@example.com', '9999999999', 'Java, SQL, JDBC, HTML', 'Java SQL JDBC HTML CSS software development'
FROM users 
WHERE username = 'candidate';

-- Insert recruiter using the user_id corresponding to 'recruiter'
INSERT IGNORE INTO recruiters(user_id, name, company_name, email)
SELECT user_id, 'Demo Recruiter', 'Demo Tech', 'recruiter@example.com'
FROM users 
WHERE username = 'recruiter';