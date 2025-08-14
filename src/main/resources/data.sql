-- Insert a test user with a specified role
INSERT INTO users (name, email, password, role) VALUES ('Test User', 'test@email.com', '$2a$10$3ierzxcG0RzOGoebyQ3I4.FIE74yNE2xKWvXqOnrNxLL2pYqMa0yO', 'user');


-- Insert a test budget for that user
INSERT INTO budgets (user_id, month_start, amount)
SELECT id, '2025-07-01', 200.00
FROM users WHERE email='test@email.com';

-- Add other data.sql inserts for expenses, alerts, etc. AFTER the user insert
-- Example for expenses:
INSERT INTO expenses (user_id, category, description, amount, date)
SELECT id, 'Food', 'Groceries for the week', 50.00, '2025-07-10'
FROM users WHERE email='test@email.com';

-- Example for alerts:
INSERT INTO alerts (user_id, message, type, is_read)
SELECT id, 'Budget exceeded for Groceries!', 'WARNING', FALSE
FROM users WHERE email='test@email.com';