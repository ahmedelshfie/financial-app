ALTER TABLE transactions
    MODIFY transaction_reference VARCHAR(50) NOT NULL,
    MODIFY transaction_type_id BIGINT NOT NULL,
    MODIFY amount DECIMAL(19,4) NOT NULL,
    MODIFY currency_code VARCHAR(3) NOT NULL,
    MODIFY status VARCHAR(20) NOT NULL;

ALTER TABLE transactions
    ADD CONSTRAINT chk_transactions_amount_positive CHECK (amount > 0),
    ADD CONSTRAINT chk_transactions_status CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REVERSED'));

CREATE INDEX idx_transactions_reference_created_at
    ON transactions (transaction_reference, created_at);
