-- src/test/resources/schema.sql
-- Explicitly create tables for tests when using a real database

-- Drop tables if they exist to ensure a clean slate for each test run
DROP TABLE IF EXISTS alerts CASCADE; -- Add this if not present
DROP TABLE IF EXISTS expenses CASCADE; -- Add this if not present
DROP TABLE IF EXISTS budgets CASCADE;
DROP TABLE IF EXISTS users CASCADE;


-- Create users table (MUST match your User entity's schema)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'client' NOT NULL
);

-- Create budgets table (MUST match your Budget entity's schema)
CREATE TABLE budgets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    -- REMOVE THIS LINE: category VARCHAR(255) NOT NULL,
    month_start DATE, -- ADD THIS LINE TO MATCH YOUR ENTITY
    amount DECIMAL(19,2) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Add CREATE TABLE statements for your other entities here if you have them:
CREATE TABLE expenses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount DECIMAL(10,2),
    category VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    date DATE NOT NULL,
    created_at TIMESTAMP(6),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE alerts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    message VARCHAR(500) NOT NULL,
    type VARCHAR(50) NOT NULL,
    is_read BOOLEAN NOT NULL,
    created_at TIMESTAMP(6),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);