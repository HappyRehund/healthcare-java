CREATE TABLE hospitals (
    hospital_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone_number VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT  CURRENT_TIMESTAMP
);

CREATE INDEX idx_hospitals_name ON hospitals(name);