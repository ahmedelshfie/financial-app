-- ============================================================================
-- Flyway Migration V1__init_schema.sql
-- Transfer Service - Initial Schema Creation
-- Description: Creates transaction tables with proper constraints, indexes, and audit fields
-- ============================================================================

-- Create transaction_types table
CREATE TABLE IF NOT EXISTS transaction_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    INDEX idx_transaction_types_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create transactions table
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_reference VARCHAR(50) NOT NULL UNIQUE,
    source_account_id BIGINT,
    destination_account_id BIGINT,
    transaction_type_id BIGINT NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL,
    description VARCHAR(512),
    initiated_by VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_transactions_type FOREIGN KEY (transaction_type_id) REFERENCES transaction_types(id),
    INDEX idx_transactions_source_account (source_account_id),
    INDEX idx_transactions_destination_account (destination_account_id),
    INDEX idx_transactions_status (status),
    INDEX idx_transactions_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seed initial transaction types
INSERT INTO transaction_types (code, name) VALUES
    ('DEPOSIT', 'Deposit'),
    ('WITHDRAWAL', 'Withdrawal'),
    ('TRANSFER', 'Transfer'),
    ('PAYMENT', 'Payment'),
    ('REFUND', 'Refund')
ON DUPLICATE KEY UPDATE name = VALUES(name);
