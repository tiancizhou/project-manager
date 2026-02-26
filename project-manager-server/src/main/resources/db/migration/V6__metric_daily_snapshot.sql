CREATE TABLE metric_daily_snapshot (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    snapshot_date DATE NOT NULL,
    total_task_count INT NOT NULL,
    completed_task_count INT NOT NULL,
    effective_hours DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_metric_daily_snapshot UNIQUE (snapshot_date)
);
