package com.rehund.healthcare.repository.hospitaldoctor;

import com.rehund.healthcare.entity.hospitaldoctor.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
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

    @Query(value = """
        SELECT * FROM doctor_availabilities da
        WHERE da.doctor_id = :doctorId
        AND da.date BETWEEN :startDate AND :endDate
        ORDER BY da.date ASC, da.start_time ASC
        """,
            nativeQuery = true
    )
    List<DoctorAvailability> findAvailableSlotsByDoctorIdAndDateRange(
            @Param("doctorId") Long doctorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(value = """
        SELECT CASE WHEN COUNT(da) > 0 THEN true ELSE false END
        FROM doctor_availabilities da
        WHERE da.doctor_id = :doctorId
        AND da.date = :date
        AND da.start_time <= :startTime
        AND da.end_time >= :endTime
        AND da.consultation_type = :consultationType
        AND da.is_available = true
        """,
            nativeQuery = true
    )
    boolean isDoctorAvailable(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("consultationType") String consultationType
    );

}
