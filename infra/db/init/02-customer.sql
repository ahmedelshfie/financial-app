-- ============================================================================
-- Customer Service Database Schema
-- ============================================================================

CREATE DATABASE IF NOT EXISTS customer_db 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE customer_db;

-- Customers table
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_code VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    national_id VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    kyc_status VARCHAR(30) NOT NULL DEFAULT 'NOT_STARTED',
    phone_number VARCHAR(20),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state_province VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(50) DEFAULT 'US',
    date_of_birth DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_customers_code (customer_code),
    INDEX idx_customers_email (email),
    INDEX idx_customers_national_id (national_id),
    INDEX idx_customers_status (status),
    INDEX idx_customers_kyc (kyc_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
