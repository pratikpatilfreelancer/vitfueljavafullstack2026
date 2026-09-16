CREATE DATABASE IF NOT EXISTS pockettrack_db;
USE pockettrack_db;

CREATE TABLE IF NOT EXISTS User (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS MonthlyBudget (
    budget_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    month INT NOT NULL,
    year INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_budget_user FOREIGN KEY (user_id)
        REFERENCES User(user_id) ON DELETE CASCADE,
    CONSTRAINT uq_budget_user_month UNIQUE (user_id, month, year)
);

CREATE TABLE IF NOT EXISTS Category (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    type ENUM('EXPENSE','INCOME') NOT NULL
);

CREATE TABLE IF NOT EXISTS `Transaction` (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    type ENUM('EXPENSE','INCOME') NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    transaction_date DATE NOT NULL,
    description VARCHAR(255),
    has_bill BOOLEAN NOT NULL DEFAULT FALSE,
    bill_image_path VARCHAR(255),
    CONSTRAINT fk_txn_user FOREIGN KEY (user_id)
        REFERENCES User(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_txn_category FOREIGN KEY (category_id)
        REFERENCES Category(category_id)
);

CREATE TABLE IF NOT EXISTS Savings (
    savings_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    balance DECIMAL(10,2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_savings_user FOREIGN KEY (user_id)
        REFERENCES User(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS SavingsLog (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    savings_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    log_type ENUM('DEPOSIT','AUTO_DEDUCT') NOT NULL,
    log_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    note VARCHAR(255),
    CONSTRAINT fk_log_savings FOREIGN KEY (savings_id)
        REFERENCES Savings(savings_id) ON DELETE CASCADE
);