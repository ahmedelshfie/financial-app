ALTER TABLE customers
    MODIFY customer_code VARCHAR(50) NOT NULL,
    MODIFY first_name VARCHAR(100) NOT NULL,
    MODIFY last_name VARCHAR(100) NOT NULL,
    MODIFY email VARCHAR(255) NOT NULL,
    MODIFY national_id VARCHAR(50) NOT NULL,
    MODIFY status VARCHAR(20) NOT NULL,
    MODIFY kyc_status VARCHAR(20) NOT NULL;

ALTER TABLE customers
    ADD CONSTRAINT chk_customers_status
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    ADD CONSTRAINT chk_customers_kyc_status
        CHECK (kyc_status IN ('PENDING', 'APPROVED', 'REJECTED'));

CREATE INDEX idx_customers_created_at ON customers (created_at);
