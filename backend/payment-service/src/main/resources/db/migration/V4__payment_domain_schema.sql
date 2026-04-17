CREATE TABLE IF NOT EXISTS beneficiaries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    bank_code VARCHAR(30) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    INDEX idx_beneficiaries_customer (customer_id)
);

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_reference VARCHAR(50) NOT NULL UNIQUE,
    source_account_id BIGINT NOT NULL,
    beneficiary_id BIGINT NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_payments_beneficiary FOREIGN KEY (beneficiary_id) REFERENCES beneficiaries(id),
    INDEX idx_payments_source_account (source_account_id),
    INDEX idx_payments_status (status)
);
