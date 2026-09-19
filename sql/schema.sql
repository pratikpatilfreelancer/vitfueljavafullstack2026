-- Run this whole file once in MySQL Workbench (or `mysql -u root -p < schema.sql`)
-- before starting the Java application.

CREATE DATABASE IF NOT EXISTS food_ordering_system;
USE food_ordering_system;

CREATE TABLE IF NOT EXISTS customer (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    phone       VARCHAR(20),
    address     VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS admin (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(100) NOT NULL,
    email    VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS food (
    food_id      INT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    category     VARCHAR(20)  NOT NULL,   -- PIZZA, BURGER, INDIAN, CHINESE, BEVERAGES, DESSERTS
    price        DECIMAL(10,2) NOT NULL,
    quantity     INT NOT NULL DEFAULT 0,
    availability BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS orders (
    order_id       INT AUTO_INCREMENT PRIMARY KEY,
    customer_id    INT NOT NULL,
    order_date     DATETIME NOT NULL,
    total_amount   DECIMAL(10,2) NOT NULL,
    status         VARCHAR(30) NOT NULL DEFAULT 'PLACED',
    payment_method VARCHAR(30),
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);

CREATE TABLE IF NOT EXISTS order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id      INT NOT NULL,
    food_id       INT NOT NULL,
    food_name     VARCHAR(100) NOT NULL,
    quantity      INT NOT NULL,
    price         DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

-- Default admin login: admin@food.com / admin123
INSERT INTO admin (name, email, password)
SELECT 'System Admin', 'admin@food.com', 'admin123'
WHERE NOT EXISTS (SELECT 1 FROM admin WHERE email = 'admin@food.com');

-- Seed menu so the UI has data to show right away.
INSERT INTO food (name, category, price, quantity, availability) VALUES
('Margherita Pizza', 'PIZZA', 250.00, 20, TRUE),
('Pepperoni Pizza',  'PIZZA', 320.00, 15, TRUE),
('Veg Burger',       'BURGER', 120.00, 30, TRUE),
('Chicken Burger',   'BURGER', 150.00, 25, TRUE),
('French Fries',     'BURGER', 100.00, 40, TRUE),
('Butter Chicken',   'INDIAN', 280.00, 18, TRUE),
('Paneer Tikka',     'INDIAN', 220.00, 20, TRUE),
('Biryani',          'INDIAN', 260.00, 15, TRUE),
('Raita',            'INDIAN', 60.00,  30, TRUE),
('Noodles',          'CHINESE', 180.00, 22, TRUE),
('Manchow Soup',     'CHINESE', 120.00, 20, TRUE),
('Spring Roll',      'CHINESE', 140.00, 18, TRUE),
('Coke',             'BEVERAGES', 60.00, 50, TRUE),
('Cold Drink',       'BEVERAGES', 60.00, 50, TRUE),
('Coffee',           'BEVERAGES', 90.00, 30, TRUE),
('Brownie',          'DESSERTS', 110.00, 20, TRUE),
('Ice Cream',        'DESSERTS', 90.00, 25, TRUE);
