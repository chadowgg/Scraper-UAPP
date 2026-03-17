CREATE TABLE images (
    id BIGSERIAL PRIMARY KEY,
    original_url VARCHAR(500) NOT NULL UNIQUE,
    original_size BIGINT,
    compressed_size BIGINT,
    compressed_file_path VARCHAR(500),
    page_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT NOW()
);