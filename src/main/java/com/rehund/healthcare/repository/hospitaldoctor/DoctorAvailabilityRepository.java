package com.rehund.healthcare.repository.hospitaldoctor;

import com.rehund.healthcare.entity.hospitaldoctor.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {

    @Query(value = """
        SELECT * FROM doctor_availabilities da
        WHERE da.doctor_id = :doctorId
        AND da.date >= CURRENT_DATE
        ORDER BY da.date ASC, da.start_time ASC
        """,
            nativeQuery = true
    )
    List<DoctorAvailability> findDoctorAvailabilitiesByDoctorIdFromToday(Long doctorId);

}
