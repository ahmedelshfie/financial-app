-- ============================================================================
-- Flyway Migration V1__init_schema.sql
-- Notification Service - Initial Schema Creation
-- Description: Creates notification tables with proper constraints and indexes
-- ============================================================================

-- Create notification_templates table
CREATE TABLE IF NOT EXISTS notification_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,
    subject_template VARCHAR(255),
    body_template TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    language VARCHAR(5) DEFAULT 'en' NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    INDEX idx_templates_code (code),
    INDEX idx_templates_type (type),
    INDEX idx_templates_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    notification_id VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    template_code VARCHAR(50),
    type VARCHAR(20) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    subject VARCHAR(255),
    body TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    sent_at DATETIME,
    delivered_at DATETIME,
    read_at DATETIME,
    failed_reason VARCHAR(512),
    retry_count INT DEFAULT 0 NOT NULL,
    max_retries INT DEFAULT 3 NOT NULL,
    priority VARCHAR(10) DEFAULT 'NORMAL' NOT NULL,
    metadata JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    INDEX idx_notifications_user_id (user_id),
    INDEX idx_notifications_status (status),
    INDEX idx_notifications_type (type),
    INDEX idx_notifications_channel (channel),
    INDEX idx_notifications_created_at (created_at),
    INDEX idx_notifications_notification_id (notification_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create notification_preferences table
CREATE TABLE IF NOT EXISTS notification_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    email_enabled BOOLEAN DEFAULT TRUE NOT NULL,
    sms_enabled BOOLEAN DEFAULT FALSE NOT NULL,
    push_enabled BOOLEAN DEFAULT TRUE NOT NULL,
    marketing_enabled BOOLEAN DEFAULT FALSE NOT NULL,
    transaction_alerts BOOLEAN DEFAULT TRUE NOT NULL,
    security_alerts BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_preferences_user UNIQUE (user_id),
    INDEX idx_preferences_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create notification_logs for audit trail
CREATE TABLE IF NOT EXISTS notification_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    notification_id BIGINT NOT NULL,
    event_type VARCHAR(20) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    request_payload TEXT,
    response_payload TEXT,
    status_code INT,
    error_message VARCHAR(512),
    processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_logs_notification FOREIGN KEY (notification_id) REFERENCES notifications(id) ON DELETE CASCADE,
    INDEX idx_logs_notification_id (notification_id),
    INDEX idx_logs_event_type (event_type),
    INDEX idx_logs_processed_at (processed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seed initial notification templates
INSERT INTO notification_templates (name, code, type, subject_template, body_template, is_active, language) VALUES
    ('Welcome Email', 'WELCOME_EMAIL', 'EMAIL', 'Welcome to FinanceApp!', 
     'Dear {{firstName}},\n\nWelcome to FinanceApp! We are excited to have you on board.\n\nBest regards,\nFinanceApp Team', 
     TRUE, 'en'),
    ('Password Reset', 'PASSWORD_RESET', 'EMAIL', 'Password Reset Request', 
     'Hello {{firstName}},\n\nYou requested a password reset. Click the link below:\n{{resetLink}}\n\nIf you did not request this, please ignore this email.', 
     TRUE, 'en'),
    ('Transaction Alert', 'TRANSACTION_ALERT', 'EMAIL', 'Transaction Notification', 
     'A {{transactionType}} of {{amount}} {{currency}} was {{status}} on your account ending {{accountNumber}}.', 
     TRUE, 'en'),
    ('Account Verification', 'ACCOUNT_VERIFICATION', 'EMAIL', 'Verify Your Account', 
     'Please verify your account by clicking: {{verificationLink}}', 
     TRUE, 'en'),
    ('Security Alert', 'SECURITY_ALERT', 'EMAIL', 'Security Alert', 
     'We detected unusual activity on your account. Please review immediately.', 
     TRUE, 'en')
ON DUPLICATE KEY UPDATE body_template = VALUES(body_template), updated_at = CURRENT_TIMESTAMP;
