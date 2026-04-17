-- ============================================================================
-- Flyway Migration V4__initial_seed_data.sql
-- Transfer Service - Initial Seed Data
-- Description: Seeds transaction type data for transfers
-- ============================================================================

-- Seed transaction types
INSERT INTO transfer_db.transaction_types (code, name, category, description, is_debit) VALUES
    ('TRANSFER', 'Funds Transfer', 'TRANSFER', 'Transfer between accounts', 1),
    ('DEPOSIT', 'Deposit', 'DEPOSIT', 'Funds deposited into account', 0),
    ('WITHDRAWAL', 'Withdrawal', 'WITHDRAWAL', 'Funds withdrawn from account', 1),
    ('CHARGE', 'Service Charge', 'FEE', 'Service or transaction fee', 1),
    ('INTEREST', 'Interest Credit', 'INTEREST', 'Interest earned on account', 0)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description);
