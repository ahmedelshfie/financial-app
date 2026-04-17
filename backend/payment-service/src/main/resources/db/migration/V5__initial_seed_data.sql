-- ============================================================================
-- Flyway Migration V5__initial_seed_data.sql
-- Payment Service - Initial Seed Data
-- Description: Seeds test beneficiaries for payment processing
-- ============================================================================

-- Seed beneficiaries for customers
-- Customer 1 (John Doe) - ID: 1
INSERT INTO payment_db.beneficiaries (customer_id, name, account_number, bank_code) VALUES
    (1, 'Alice Johnson', '9876543210', 'BANK-USA'),
    (1, 'Bob Smith', '1122334455', 'BANK-INTL')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    account_number = VALUES(account_number);

-- Customer 2 (Jane Smith) - ID: 2
INSERT INTO payment_db.beneficiaries (customer_id, name, account_number, bank_code) VALUES
    (2, 'Charlie Brown', '5544332211', 'BANK-USA'),
    (2, 'Diana Prince', '6677889900', 'BANK-INTL')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    account_number = VALUES(account_number);

-- Customer 3 (Michael Johnson) - ID: 3
INSERT INTO payment_db.beneficiaries (customer_id, name, account_number, bank_code) VALUES
    (3, 'Edward Norton', '1111222233', 'BANK-USA'),
    (3, 'Fiona Green', '4444555566', 'BANK-INTL')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    account_number = VALUES(account_number);

-- Customer 4 (Sarah Williams) - ID: 4
INSERT INTO payment_db.beneficiaries (customer_id, name, account_number, bank_code) VALUES
    (4, 'George Wilson', '7777888899', 'BANK-USA'),
    (4, 'Helen Troy', '2222333344', 'BANK-INTL')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    account_number = VALUES(account_number);

-- Customer 5 (Robert Brown) - ID: 5
INSERT INTO payment_db.beneficiaries (customer_id, name, account_number, bank_code) VALUES
    (5, 'Ivan Martinez', '5555666677', 'BANK-USA'),
    (5, 'Julia Roberts', '8888999900', 'BANK-INTL')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    account_number = VALUES(account_number);

-- Customer 6 (Emily Davis) - ID: 6
INSERT INTO payment_db.beneficiaries (customer_id, name, account_number, bank_code) VALUES
    (6, 'Kevin Stone', '3333444455', 'BANK-USA'),
    (6, 'Laura Palmer', '1234567890', 'BANK-INTL')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    account_number = VALUES(account_number);

-- Customer 7 (David Miller) - ID: 7
INSERT INTO payment_db.beneficiaries (customer_id, name, account_number, bank_code) VALUES
    (7, 'Michael Scott', '0987654321', 'BANK-USA'),
    (7, 'Nancy Drew', '1357924680', 'BANK-INTL')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    account_number = VALUES(account_number);

-- Customer 8 (Lisa Wilson) - ID: 8
INSERT INTO payment_db.beneficiaries (customer_id, name, account_number, bank_code) VALUES
    (8, 'Oscar Wilde', '2468013579', 'BANK-USA'),
    (8, 'Piper Chapman', '9876543211', 'BANK-INTL')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    account_number = VALUES(account_number);

-- Customer 9 (James Moore) - ID: 9
INSERT INTO payment_db.beneficiaries (customer_id, name, account_number, bank_code) VALUES
    (9, 'Quinn Adams', '1122334456', 'BANK-USA'),
    (9, 'Rachel Green', '5544332212', 'BANK-INTL')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    account_number = VALUES(account_number);

-- Customer 10 (Patricia Taylor) - ID: 10
INSERT INTO payment_db.beneficiaries (customer_id, name, account_number, bank_code) VALUES
    (10, 'Sam Fisher', '6677889901', 'BANK-USA'),
    (10, 'Tina Turner', '1111222234', 'BANK-INTL')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    account_number = VALUES(account_number);
