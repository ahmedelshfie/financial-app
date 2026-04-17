CREATE TABLE IF NOT EXISTS account_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    minimum_balance DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    daily_transfer_limit DECIMAL(19,4) NOT NULL DEFAULT 0.0000
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    account_type_id BIGINT NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    current_balance DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    available_balance DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    opened_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_accounts_account_type FOREIGN KEY (account_type_id) REFERENCES account_types(id),
    INDEX idx_accounts_customer_id (customer_id),
    INDEX idx_accounts_status (status),
    INDEX idx_accounts_account_type_id (account_type_id),
    INDEX idx_accounts_opened_at (opened_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO account_types (code, name, minimum_balance, daily_transfer_limit) VALUES
    ('CHECKING', 'Checking Account', 0.0000, 10000.0000),
    ('SAVINGS', 'Savings Account', 100.0000, 5000.0000)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    minimum_balance = VALUES(minimum_balance),
    daily_transfer_limit = VALUES(daily_transfer_limit);
