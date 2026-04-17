ALTER TABLE refresh_tokens
    MODIFY token VARCHAR(512) NOT NULL,
    MODIFY expiry_date DATETIME NOT NULL,
    MODIFY revoked BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX idx_refresh_tokens_user_revoked_expiry
    ON refresh_tokens (user_id, revoked, expiry_date);
