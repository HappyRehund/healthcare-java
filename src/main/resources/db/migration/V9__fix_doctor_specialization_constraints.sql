-- Fix unique constraint to allow both ONLINE and OFFLINE for same doctor_specialization
ALTER TABLE hospital_doctor_fees
DROP CONSTRAINT hospital_doctor_fees_hospital_id_doctor_specialization_id_key;

ALTER TABLE hospital_doctor_fees
    ADD CONSTRAINT hospital_doctor_fees_unique
        UNIQUE (hospital_id, doctor_specialization_id, consultation_type);