CREATE INDEX idx_transactions_source_status_created
    ON transactions (source_account_id, status, created_at);

CREATE INDEX idx_transactions_destination_status_created
    ON transactions (destination_account_id, status, created_at);
