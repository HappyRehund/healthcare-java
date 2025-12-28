package com.rehund.healthcare.repository.appointment;

import com.rehund.healthcare.entity.appointment.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // untuk cek jadwal bentrok pada saat pembuatan atau pembaruan janji temu (Locking untuk konsistensi data)
    @Query(
            value = """
            SELECT * FROM appointments
            WHERE doctor_id = :doctorId
            AND appointment_date = :appointmentDate
            AND consultation_type = :consultationType
            AND status = 'SCHEDULED'
            AND ((start_time < :endTime AND end_time > :startTime)
                OR (start_time = :startTime AND end_time = :endTime))
            FOR UPDATE
            """,
            nativeQuery = true
    )
    List<Appointment> findOverlappingAppointments(
            @Param("doctorId") Long doctorId,
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("consultationType") String consultationType
    );

    @Query(
            value = """
            SELECT * FROM appointments
            WHERE doctor_id = :doctorId
            AND appointment_date BETWEEN :startDate AND :endDate
            ORDER BY appointment_date ASC, start_time ASC
            """,
            nativeQuery = true
    )
    List<Appointment> findDoctorAppointmentsInDateRange(
            @Param("doctorId") Long doctorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    List<Appointment> findByPatientIdOrderByAppointmentDateDescStartTimeDesc(Long patientId);
    List<Appointment> findByDoctorIdAndAppointmentDateOrderByStartTimeAsc(Long doctorId, LocalDate appointmentDate);
}
