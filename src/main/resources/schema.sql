CREATE TABLE IF NOT EXISTS file_upload (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    original_file_name VARCHAR(255) NOT NULL,
    stored_file_name   VARCHAR(255) NOT NULL,
    file_size          BIGINT       NOT NULL,
    content_type       VARCHAR(100) NOT NULL,
    storage_path       VARCHAR(500) NOT NULL,
    uploaded_by        VARCHAR(100) NOT NULL,
    status             VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
