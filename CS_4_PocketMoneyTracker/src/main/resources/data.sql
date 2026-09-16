INSERT IGNORE INTO Category (category_id, name, type) VALUES
(1, 'Food', 'EXPENSE'),
(2, 'Travel', 'EXPENSE'),
(3, 'Shopping', 'EXPENSE'),
(4, 'Education', 'EXPENSE'),
(5, 'Entertainment', 'EXPENSE'),
(6, 'Bills', 'EXPENSE'),
(7, 'Health', 'EXPENSE'),
(8, 'Other Expense', 'EXPENSE'),
(9, 'Extra Income', 'INCOME'),
(10, 'Gift', 'INCOME'),
(11, 'Other Income', 'INCOME');

INSERT IGNORE INTO User (user_id, name, email, password)
VALUES (1, 'Demo User', 'demo@pockettrack.com', 'password');

INSERT IGNORE INTO Savings (user_id, balance)
VALUES (1, 0.00);