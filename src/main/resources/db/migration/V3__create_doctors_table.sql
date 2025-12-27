CREATE TABLE specializations (
    specialization_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE doctors (
    doctor_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    hospital_id BIGINT NOT NULL,
    bio TEXT,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (hospital_id) REFERENCES hospitals(hospital_id)
);

CREATE TABLE doctor_specializations (
    doctor_specialization_id BIGSERIAL PRIMARY KEY,

    doctor_id BIGINT NOT NULL,
    specialization_id BIGINT NOT NULL,
    base_fee DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id),
    FOREIGN KEY (specialization_id) REFERENCES specializations(specialization_id),
    UNIQUE (doctor_id, specialization_id)
);

CREATE TABLE hospital_doctor_fees (
    hospital_doctor_fee_id BIGSERIAL PRIMARY KEY,

    hospital_id BIGINT NOT NULL,
    doctor_specialization_id BIGINT NOT NULL,
    fee DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (hospital_id) REFERENCES hospitals(hospital_id),
    FOREIGN KEY (doctor_specialization_id) REFERENCES doctor_specializations(doctor_specialization_id),
    UNIQUE (hospital_id, doctor_specialization_id)
);

CREATE TABLE doctor_availabilities (
    doctor_availability_id BIGSERIAL PRIMARY KEY,

    doctor_id BIGINT NOT NULL,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    consultation_type VARCHAR(50) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id)
);

CREATE INDEX idx_doctors_user_id ON doctors(user_id);
CREATE INDEX idx_doctors_hospital_id ON doctors(hospital_id);

CREATE INDEX idx_doctor_specializations_doctor_id ON doctor_specializations(doctor_id);
CREATE INDEX idx_doctor_specializations_specialization_id ON doctor_specializations(specialization_id);

CREATE INDEX idx_hospital_doctor_fees_hospital_id ON hospital_doctor_fees(hospital_id);

CREATE INDEX idx_hospital_doctor_fees_doctor_specialization_id ON hospital_doctor_fees(doctor_specialization_id);

CREATE INDEX idx_doctor_availabilities_doctor_id ON doctor_availabilities(doctor_id);
CREATE INDEX idx_doctor_availabilities_date ON doctor_availabilities(date);

