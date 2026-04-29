-- =============================================================
-- TEST USER
-- =============================================================
INSERT INTO users (name, email, password, role)
VALUES ('Test User', 'test@email.com', '$2a$10$3ierzxcG0RzOGoebyQ3I4.FIE74yNE2xKWvXqOnrNxLL2pYqMa0yO', 'user')
ON CONFLICT (email) DO NOTHING;

-- =============================================================
-- CATEGORIES
-- =============================================================
INSERT INTO categories (name) VALUES ('Groceries')     ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Rent')          ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Utilities')     ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Transportation') ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Entertainment') ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Dining Out')    ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Health')        ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Shopping')      ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Personal Care') ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Travel')        ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Education')     ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Miscellaneous') ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Subscriptions') ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Housing')       ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Food')          ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Gifts')         ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Other')         ON CONFLICT (name) DO NOTHING;

-- =============================================================
-- BUDGETS  (one per category, month_start = 2025-01-01)
-- =============================================================
INSERT INTO budgets (user_id, category_id, month_start, amount)
SELECT u.id, c.id, '2025-01-01', 400.00
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Groceries';

INSERT INTO budgets (user_id, category_id, month_start, amount)
SELECT u.id, c.id, '2025-01-01', 1500.00
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Rent';

INSERT INTO budgets (user_id, category_id, month_start, amount)
SELECT u.id, c.id, '2025-01-01', 200.00
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Utilities';

INSERT INTO budgets (user_id, category_id, month_start, amount)
SELECT u.id, c.id, '2025-01-01', 150.00
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Transportation';

INSERT INTO budgets (user_id, category_id, month_start, amount)
SELECT u.id, c.id, '2025-01-01', 100.00
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Entertainment';

INSERT INTO budgets (user_id, category_id, month_start, amount)
SELECT u.id, c.id, '2025-01-01', 250.00
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Dining Out';

INSERT INTO budgets (user_id, category_id, month_start, amount)
SELECT u.id, c.id, '2025-01-01', 120.00
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Health';

INSERT INTO budgets (user_id, category_id, month_start, amount)
SELECT u.id, c.id, '2025-01-01', 200.00
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Shopping';

INSERT INTO budgets (user_id, category_id, month_start, amount)
SELECT u.id, c.id, '2025-01-01', 80.00
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Personal Care';

INSERT INTO budgets (user_id, category_id, month_start, amount)
SELECT u.id, c.id, '2025-01-01', 500.00
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Travel';

INSERT INTO budgets (user_id, category_id, month_start, amount)
SELECT u.id, c.id, '2025-01-01', 150.00
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Education';

INSERT INTO budgets (user_id, category_id, month_start, amount)
SELECT u.id, c.id, '2025-01-01', 75.00
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Miscellaneous';

INSERT INTO budgets (user_id, category_id, month_start, amount)
SELECT u.id, c.id, '2025-01-01', 60.00
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Subscriptions';

INSERT INTO budgets (user_id, category_id, month_start, amount)
SELECT u.id, c.id, '2025-01-01', 300.00
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Housing';

INSERT INTO budgets (user_id, category_id, month_start, amount)
SELECT u.id, c.id, '2025-01-01', 350.00
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Food';

INSERT INTO budgets (user_id, category_id, month_start, amount)
SELECT u.id, c.id, '2025-01-01', 100.00
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Gifts';

INSERT INTO budgets (user_id, category_id, month_start, amount)
SELECT u.id, c.id, '2025-01-01', 50.00
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Other';

-- =============================================================
-- EXPENSES
-- =============================================================

-- Groceries
INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Weekly grocery run', 85.50, '2025-01-06'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Groceries';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Supermarket top-up', 42.30, '2025-02-14'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Groceries';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Farmers market produce', 31.00, '2025-04-05'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Groceries';

-- Rent
INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'January rent payment', 1500.00, '2025-01-01'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Rent';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'February rent payment', 1500.00, '2025-02-01'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Rent';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'March rent payment', 1500.00, '2025-03-01'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Rent';

-- Utilities
INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Electricity bill', 78.40, '2025-01-15'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Utilities';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Water and sewage bill', 45.00, '2025-02-15'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Utilities';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Internet service', 59.99, '2025-03-10'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Utilities';

-- Transportation
INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Monthly bus pass', 65.00, '2025-01-03'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Transportation';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Fuel fill-up', 52.80, '2025-02-20'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Transportation';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Uber rides this week', 28.50, '2025-04-11'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Transportation';

-- Entertainment
INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Cinema tickets', 24.00, '2025-01-18'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Entertainment';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Concert tickets', 55.00, '2025-03-22'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Entertainment';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Board game purchase', 34.99, '2025-05-09'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Entertainment';

-- Dining Out
INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Dinner at Italian restaurant', 62.00, '2025-01-25'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Dining Out';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Lunch with colleagues', 28.50, '2025-02-28'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Dining Out';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Brunch at cafe', 19.90, '2025-04-19'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Dining Out';

-- Health
INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Gym membership', 40.00, '2025-01-05'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Health';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Pharmacy — vitamins and supplements', 33.75, '2025-02-10'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Health';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Doctor co-pay', 25.00, '2025-05-02'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Health';

-- Shopping
INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Winter jacket', 89.99, '2025-01-12'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Shopping';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Shoes', 64.00, '2025-03-08'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Shopping';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Home decor items', 47.50, '2025-05-17'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Shopping';

-- Personal Care
INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Haircut', 30.00, '2025-01-20'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Personal Care';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Skincare products', 22.40, '2025-03-14'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Personal Care';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Toiletries restock', 18.60, '2025-05-05'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Personal Care';

-- Travel
INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Flight to New York', 210.00, '2025-02-05'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Travel';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Hotel stay — 2 nights', 180.00, '2025-02-07'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Travel';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Travel insurance', 45.00, '2025-04-28'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Travel';

-- Education
INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Online course subscription', 49.00, '2025-01-08'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Education';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Textbooks', 72.00, '2025-02-03'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Education';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Workshop registration fee', 35.00, '2025-04-15'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Education';

-- Miscellaneous
INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Postage and shipping', 12.50, '2025-01-22'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Miscellaneous';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Stationery supplies', 15.80, '2025-03-17'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Miscellaneous';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Donation to charity', 20.00, '2025-05-20'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Miscellaneous';

-- Subscriptions
INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Netflix monthly', 15.99, '2025-01-01'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Subscriptions';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Spotify premium', 9.99, '2025-02-01'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Subscriptions';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Cloud storage plan', 2.99, '2025-03-01'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Subscriptions';

-- Housing
INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Plumber repair', 120.00, '2025-01-29'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Housing';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Cleaning supplies', 38.00, '2025-03-05'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Housing';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'New curtains', 75.00, '2025-05-12'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Housing';

-- Food
INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Groceries for the week', 50.00, '2025-01-10'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Food';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Meal prep ingredients', 44.20, '2025-03-03'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Food';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Snacks and beverages', 21.60, '2025-05-08'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Food';

-- Gifts
INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Birthday gift for friend', 45.00, '2025-02-18'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Gifts';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Valentine''s Day flowers', 30.00, '2025-02-14'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Gifts';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Wedding gift', 80.00, '2025-04-26'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Gifts';

-- Other
INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Miscellaneous purchase', 18.00, '2025-01-30'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Other';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Unexpected expense', 27.50, '2025-03-25'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Other';

INSERT INTO expenses (user_id, category_id, description, amount, date)
SELECT u.id, c.id, 'Unplanned repair', 55.00, '2025-05-22'
FROM users u, categories c WHERE u.email='test@email.com' AND c.name='Other';

-- =============================================================
-- ALERTS
-- =============================================================
INSERT INTO alerts (user_id, message, type, is_read)
SELECT id, 'Budget exceeded for Groceries!', 'WARNING', FALSE
FROM users WHERE email='test@email.com';

INSERT INTO alerts (user_id, message, type, is_read)
SELECT id, 'You are close to your Dining Out budget limit.', 'WARNING', FALSE
FROM users WHERE email='test@email.com';

INSERT INTO alerts (user_id, message, type, is_read)
SELECT id, 'Rent payment recorded for March.', 'INFO', TRUE
FROM users WHERE email='test@email.com';
