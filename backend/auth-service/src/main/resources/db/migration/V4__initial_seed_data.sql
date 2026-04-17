-- ============================================================================
-- Flyway Migration V4__initial_seed_data.sql
-- Auth Service - Initial Seed Data
-- Description: Seeds roles and default users for testing and operations
-- ============================================================================

-- Seed roles
INSERT INTO auth_db.roles (name, description) VALUES
    ('ADMIN', 'Administrator with full system access'),
    ('USER', 'Regular user with basic permissions'),
    ('MANAGER', 'Manager with approval and reporting permissions'),
    ('TELLER', 'Teller with transaction processing permissions')
ON DUPLICATE KEY UPDATE
    description = VALUES(description);

-- Seed default users (passwords should be securely hashed in production)
-- Example: password123 hashed with bcrypt
INSERT INTO auth_db.users (username, email, password_hash, status, first_name, last_name, phone, failed_login_attempts) VALUES
    ('admin', 'admin@financial-platform.com', '$2a$10$slYQmyNdGzin7olVN3p5be7DlH.PKZbv5H8KnzzVgXXbVxzy70jey', 'ACTIVE', 'Admin', 'User', '+1-555-0100', 0),
    ('user1', 'user1@financial-platform.com', '$2a$10$slYQmyNdGzin7olVN3p5be7DlH.PKZbv5H8KnzzVgXXbVxzy70jey', 'ACTIVE', 'John', 'Doe', '+1-555-0101', 0),
    ('user2', 'user2@financial-platform.com', '$2a$10$slYQmyNdGzin7olVN3p5be7DlH.PKZbv5H8KnzzVgXXbVxzy70jey', 'ACTIVE', 'Jane', 'Smith', '+1-555-0102', 0),
    ('manager1', 'manager1@financial-platform.com', '$2a$10$slYQmyNdGzin7olVN3p5be7DlH.PKZbv5H8KnzzVgXXbVxzy70jey', 'ACTIVE', 'Mike', 'Johnson', '+1-555-0103', 0),
    ('teller1', 'teller1@financial-platform.com', '$2a$10$slYQmyNdGzin7olVN3p5be7DlH.PKZbv5H8KnzzVgXXbVxzy70jey', 'ACTIVE', 'Sarah', 'Williams', '+1-555-0104', 0)
ON DUPLICATE KEY UPDATE
    email = VALUES(email),
    status = VALUES(status),
    first_name = VALUES(first_name),
    last_name = VALUES(last_name);

-- Assign roles to users
-- Get role IDs and user IDs dynamically
SET @adminUserId = (SELECT id FROM auth_db.users WHERE username = 'admin');
SET @userRole = (SELECT id FROM auth_db.roles WHERE name = 'USER');
SET @adminRole = (SELECT id FROM auth_db.roles WHERE name = 'ADMIN');
SET @managerRole = (SELECT id FROM auth_db.roles WHERE name = 'MANAGER');
SET @tellerRole = (SELECT id FROM auth_db.roles WHERE name = 'TELLER');

INSERT INTO auth_db.user_roles (user_id, role_id) VALUES
    (@adminUserId, @adminRole)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

SET @user1Id = (SELECT id FROM auth_db.users WHERE username = 'user1');
INSERT INTO auth_db.user_roles (user_id, role_id) VALUES
    (@user1Id, @userRole)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

SET @user2Id = (SELECT id FROM auth_db.users WHERE username = 'user2');
INSERT INTO auth_db.user_roles (user_id, role_id) VALUES
    (@user2Id, @userRole)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

SET @manager1Id = (SELECT id FROM users WHERE username = 'manager1');
INSERT INTO user_roles (user_id, role_id) VALUES
    (@manager1Id, @managerRole)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

SET @teller1Id = (SELECT id FROM users WHERE username = 'teller1');
INSERT INTO user_roles (user_id, role_id) VALUES
    (@teller1Id, @tellerRole)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);
