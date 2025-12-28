ALTER TABLE hospital_doctor_fees
ADD COLUMN consultation_type VARCHAR(10) NOT NULL DEFAULT 'OFFLINE';