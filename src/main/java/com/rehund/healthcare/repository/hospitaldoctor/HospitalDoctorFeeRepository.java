package com.rehund.healthcare.repository.hospitaldoctor;

import com.rehund.healthcare.entity.hospitaldoctor.DoctorSpecialization;
import com.rehund.healthcare.entity.hospitaldoctor.HospitalDoctorFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalDoctorFeeRepository extends JpaRepository<HospitalDoctorFee, Long> {
    Optional<HospitalDoctorFee> findByHospitalIdAndDoctorSpecializationId(Long hospitalId, Long doctorSpecializationId);

    boolean existsByHospitalIdAndDoctorSpecializationIdAndConsultationType(
            Long hospitalId,
            Long doctorSpecializationId,
            String consultationType
    );

    List<HospitalDoctorFee> findAllByHospitalIdAndDoctorSpecializationId(Long hospitalId, Long doctorSpecializationId);

    Optional<HospitalDoctorFee> findByHospitalIdAndDoctorSpecializationIdAndConsultationType(
            Long hospitalId,
            Long doctorSpecializationId,
            String consultationType
    );
}
