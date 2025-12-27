package com.rehund.healthcare.repository.hospitaldoctor;

import com.rehund.healthcare.entity.hospitaldoctor.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {

}
