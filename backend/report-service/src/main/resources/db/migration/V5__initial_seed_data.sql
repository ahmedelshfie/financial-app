-- ============================================================================
-- Flyway Migration V5__initial_seed_data.sql
-- Report Service - Initial Seed Data
-- Description: Seeds transaction type data for reports
-- ============================================================================

-- Seed transaction types (referenced in reports)
INSERT INTO report_db.transaction_types (code, name, category, description, is_debit) VALUES
    ('TRANSFER', 'Funds Transfer', 'TRANSFER', 'Transfer between accounts', 1),
    ('DEPOSIT', 'Deposit', 'DEPOSIT', 'Funds deposited into account', 0),
    ('WITHDRAWAL', 'Withdrawal', 'WITHDRAWAL', 'Funds withdrawn from account', 1),
    ('CHARGE', 'Service Charge', 'FEE', 'Service or transaction fee', 1),
    ('INTEREST', 'Interest Credit', 'INTEREST', 'Interest earned on account', 0),
    ('PAYMENT', 'Payment Out', 'PAYMENT', 'Payment to third party', 1),
    ('REFUND', 'Refund', 'REFUND', 'Refund received', 0)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description);

-- Seed report types/templates
INSERT INTO report_db.report_types (code, name, description) VALUES
    ('ACCOUNT_SUMMARY', 'Account Summary Report', 'Summary of account balances and transactions'),
    ('TRANSACTION_DETAIL', 'Transaction Detail Report', 'Detailed listing of all transactions'),
    ('CUSTOMER_ACTIVITY', 'Customer Activity Report', 'Customer activity and engagement metrics'),
    ('PAYMENT_REPORT', 'Payment Report', 'Payment initiation and completion summary'),
    ('TRANSFER_REPORT', 'Transfer Report', 'Internal transfer activity report'),
    ('COMPLIANCE_REPORT', 'Compliance Report', 'Transaction and activity compliance reporting')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description);
