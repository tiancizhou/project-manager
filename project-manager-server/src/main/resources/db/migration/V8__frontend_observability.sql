CREATE TABLE frontend_observability_event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(128) NOT NULL,
    level VARCHAR(16) NOT NULL,
    occurred_at TIMESTAMP NOT NULL,
    payload_json CLOB NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_frontend_observability_event_occurred_at
    ON frontend_observability_event (occurred_at);
