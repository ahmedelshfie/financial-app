-- ============================================================================
-- Flyway Migration V3__initial_seed_data.sql
-- Customer Service - Initial Seed Data
-- Description: Seeds test customers for development and testing
-- ============================================================================

INSERT INTO customers (customer_code, first_name, last_name, email, national_id, status, kyc_status) VALUES
    ('CUST-0001', 'John', 'Doe', 'john.doe@example.com', 'ID-000001', 'ACTIVE', 'VERIFIED'),
    ('CUST-0002', 'Jane', 'Smith', 'jane.smith@example.com', 'ID-000002', 'ACTIVE', 'VERIFIED'),
    ('CUST-0003', 'Michael', 'Johnson', 'michael.johnson@example.com', 'ID-000003', 'ACTIVE', 'VERIFIED'),
    ('CUST-0004', 'Sarah', 'Williams', 'sarah.williams@example.com', 'ID-000004', 'ACTIVE', 'VERIFIED'),
    ('CUST-0005', 'Robert', 'Brown', 'robert.brown@example.com', 'ID-000005', 'ACTIVE', 'VERIFIED'),
    ('CUST-0006', 'Emily', 'Davis', 'emily.davis@example.com', 'ID-000006', 'ACTIVE', 'VERIFIED'),
    ('CUST-0007', 'David', 'Miller', 'david.miller@example.com', 'ID-000007', 'ACTIVE', 'VERIFIED'),
    ('CUST-0008', 'Lisa', 'Wilson', 'lisa.wilson@example.com', 'ID-000008', 'ACTIVE', 'VERIFIED'),
    ('CUST-0009', 'James', 'Moore', 'james.moore@example.com', 'ID-000009', 'ACTIVE', 'VERIFIED'),
    ('CUST-0010', 'Patricia', 'Taylor', 'patricia.taylor@example.com', 'ID-000010', 'ACTIVE', 'VERIFIED')
ON DUPLICATE KEY UPDATE
    email = VALUES(email),
    status = VALUES(status),
    kyc_status = VALUES(kyc_status);
