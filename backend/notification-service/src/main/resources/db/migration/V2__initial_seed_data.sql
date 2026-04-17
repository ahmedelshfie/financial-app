-- ============================================================================
-- Flyway Migration V2__initial_seed_data.sql
-- Notification Service - Initial Seed Data
-- Description: Seeds notification templates for alert messages
-- ============================================================================

-- Seed notification templates
INSERT INTO notification_templates (name, subject_template, body_template, notification_type) VALUES
    ('TRANSFER_INITIATED', 'Transfer Initiated', 'Your transfer of {amount} {currency} to {recipient} has been initiated. Reference: {reference}', 'EMAIL'),
    ('TRANSFER_COMPLETED', 'Transfer Completed', 'Your transfer of {amount} {currency} to {recipient} has been completed successfully. Reference: {reference}', 'EMAIL'),
    ('TRANSFER_FAILED', 'Transfer Failed', 'Your transfer of {amount} {currency} to {recipient} has failed. Error: {error}. Reference: {reference}', 'EMAIL'),
    ('PAYMENT_INITIATED', 'Payment Initiated', 'Payment of {amount} {currency} to {beneficiary} has been initiated. Reference: {reference}', 'SMS'),
    ('PAYMENT_COMPLETED', 'Payment Completed', 'Payment of {amount} {currency} to {beneficiary} has been completed successfully. Reference: {reference}', 'SMS'),
    ('ACCOUNT_ALERT', 'Account Alert', 'Alert: {message}. Please review your account for any unusual activities.', 'EMAIL'),
    ('LOW_BALANCE', 'Low Balance Warning', 'Your account balance has fallen below the minimum threshold. Current balance: {balance} {currency}', 'SMS'),
    ('NEW_BENEFICIARY', 'Beneficiary Added', 'A new beneficiary {name} has been added to your account.', 'EMAIL')
ON DUPLICATE KEY UPDATE
    subject_template = VALUES(subject_template),
    body_template = VALUES(body_template),
    notification_type = VALUES(notification_type);
