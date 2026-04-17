ALTER TABLE accounts
    MODIFY account_number VARCHAR(20) NOT NULL,
    MODIFY customer_id BIGINT NOT NULL,
    MODIFY account_type_id BIGINT NOT NULL,
    MODIFY currency_code VARCHAR(3) NOT NULL,
    MODIFY current_balance DECIMAL(19,4) NOT NULL,
    MODIFY available_balance DECIMAL(19,4) NOT NULL,
    MODIFY status VARCHAR(20) NOT NULL;

ALTER TABLE accounts
    ADD CONSTRAINT chk_accounts_non_negative_balances CHECK (current_balance >= 0 AND available_balance >= 0),
    ADD CONSTRAINT chk_accounts_status CHECK (status IN ('ACTIVE', 'BLOCKED', 'CLOSED')),
    ADD CONSTRAINT chk_accounts_currency CHECK (CHAR_LENGTH(currency_code) = 3);

CREATE INDEX idx_accounts_customer_status ON accounts (customer_id, status);
