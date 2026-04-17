CREATE TABLE IF NOT EXISTS report_snapshots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    report_name VARCHAR(100) NOT NULL,
    request_key VARCHAR(120) NOT NULL,
    payload_json JSON NOT NULL,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UNIQUE KEY uq_report_request (report_name, request_key)
);
