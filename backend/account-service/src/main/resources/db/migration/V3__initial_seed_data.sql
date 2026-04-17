-- ============================================================================
-- Flyway Migration V3__initial_seed_data.sql
-- Account Service - Initial Seed Data
-- Description: Seeds test accounts for customers with initial balances
-- ============================================================================

-- Seed checking and savings accounts for each customer
-- Customer 1 (John Doe) - ID: 1
INSERT INTO account_db.accounts (account_number, customer_id, account_type_id, currency_code, current_balance, available_balance, status) VALUES
    ('CHK-0001001', 1, 1, 'USD', 5000.00, 5000.00, 'ACTIVE'),
    ('SAV-0001002', 1, 2, 'USD', 10000.00, 10000.00, 'ACTIVE')
ON DUPLICATE KEY UPDATE
    current_balance = VALUES(current_balance),
    available_balance = VALUES(available_balance),
    status = VALUES(status);

-- Customer 2 (Jane Smith) - ID: 2
INSERT INTO account_db.accounts (account_number, customer_id, account_type_id, currency_code, current_balance, available_balance, status) VALUES
    ('CHK-0002001', 2, 1, 'USD', 3500.00, 3500.00, 'ACTIVE'),
    ('SAV-0002002', 2, 2, 'USD', 8500.00, 8500.00, 'ACTIVE')
ON DUPLICATE KEY UPDATE
    current_balance = VALUES(current_balance),
    available_balance = VALUES(available_balance),
    status = VALUES(status);

-- Customer 3 (Michael Johnson) - ID: 3
INSERT INTO account_db.accounts (account_number, customer_id, account_type_id, currency_code, current_balance, available_balance, status) VALUES
    ('CHK-0003001', 3, 1, 'USD', 7200.50, 7200.50, 'ACTIVE'),
    ('SAV-0003002', 3, 2, 'USD', 15000.00, 15000.00, 'ACTIVE')
ON DUPLICATE KEY UPDATE
    current_balance = VALUES(current_balance),
    available_balance = VALUES(available_balance),
    status = VALUES(status);

-- Customer 4 (Sarah Williams) - ID: 4
INSERT INTO account_db.accounts (account_number, customer_id, account_type_id, currency_code, current_balance, available_balance, status) VALUES
    ('CHK-0004001', 4, 1, 'USD', 2500.00, 2500.00, 'ACTIVE'),
    ('SAV-0004002', 4, 2, 'USD', 20000.00, 20000.00, 'ACTIVE')
ON DUPLICATE KEY UPDATE
    current_balance = VALUES(current_balance),
    available_balance = VALUES(available_balance),
    status = VALUES(status);

-- Customer 5 (Robert Brown) - ID: 5
INSERT INTO account_db.accounts (account_number, customer_id, account_type_id, currency_code, current_balance, available_balance, status) VALUES
    ('CHK-0005001', 5, 1, 'USD', 4300.75, 4300.75, 'ACTIVE'),
    ('SAV-0005002', 5, 2, 'USD', 12500.00, 12500.00, 'ACTIVE')
ON DUPLICATE KEY UPDATE
    current_balance = VALUES(current_balance),
    available_balance = VALUES(available_balance),
    status = VALUES(status);

-- Customer 6 (Emily Davis) - ID: 6
INSERT INTO account_db.accounts (account_number, customer_id, account_type_id, currency_code, current_balance, available_balance, status) VALUES
    ('CHK-0006001', 6, 1, 'USD', 6100.00, 6100.00, 'ACTIVE'),
    ('SAV-0006002', 6, 2, 'USD', 25000.00, 25000.00, 'ACTIVE')
ON DUPLICATE KEY UPDATE
    current_balance = VALUES(current_balance),
    available_balance = VALUES(available_balance),
    status = VALUES(status);

-- Customer 7 (David Miller) - ID: 7
INSERT INTO account_db.accounts (account_number, customer_id, account_type_id, currency_code, current_balance, available_balance, status) VALUES
    ('CHK-0007001', 7, 1, 'USD', 3200.00, 3200.00, 'ACTIVE'),
    ('SAV-0007002', 7, 2, 'USD', 9500.00, 9500.00, 'ACTIVE')
ON DUPLICATE KEY UPDATE
    current_balance = VALUES(current_balance),
    available_balance = VALUES(available_balance),
    status = VALUES(status);

-- Customer 8 (Lisa Wilson) - ID: 8
INSERT INTO account_db.accounts (account_number, customer_id, account_type_id, currency_code, current_balance, available_balance, status) VALUES
    ('CHK-0008001', 8, 1, 'USD', 8500.50, 8500.50, 'ACTIVE'),
    ('SAV-0008002', 8, 2, 'USD', 18000.00, 18000.00, 'ACTIVE')
ON DUPLICATE KEY UPDATE
    current_balance = VALUES(current_balance),
    available_balance = VALUES(available_balance),
    status = VALUES(status);

-- Customer 9 (James Moore) - ID: 9
INSERT INTO account_db.accounts (account_number, customer_id, account_type_id, currency_code, current_balance, available_balance, status) VALUES
    ('CHK-0009001', 9, 1, 'USD', 4150.00, 4150.00, 'ACTIVE'),
    ('SAV-0009002', 9, 2, 'USD', 11000.00, 11000.00, 'ACTIVE')
ON DUPLICATE KEY UPDATE
    current_balance = VALUES(current_balance),
    available_balance = VALUES(available_balance),
    status = VALUES(status);

-- Customer 10 (Patricia Taylor) - ID: 10
INSERT INTO account_db.accounts (account_number, customer_id, account_type_id, currency_code, current_balance, available_balance, status) VALUES
    ('CHK-0010001', 10, 1, 'USD', 5800.75, 5800.75, 'ACTIVE'),
    ('SAV-0010002', 10, 2, 'USD', 22000.00, 22000.00, 'ACTIVE')
ON DUPLICATE KEY UPDATE
    current_balance = VALUES(current_balance),
    available_balance = VALUES(available_balance),
    status = VALUES(status);
