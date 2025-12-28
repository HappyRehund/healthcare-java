package com.rehund.healthcare.repository.hospitaldoctor;

import com.rehund.healthcare.entity.hospitaldoctor.DoctorSpecialization;
import com.rehund.healthcare.entity.hospitaldoctor.HospitalDoctorFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalDoctorFeeRepository extends JpaRepository<HospitalDoctorFee, Long> {
    Optional<HospitalDoctorFee> findByHospitalIdAndDoctorSpecializationId(Long hospitalId, Long doctorSpecializationId);
}
