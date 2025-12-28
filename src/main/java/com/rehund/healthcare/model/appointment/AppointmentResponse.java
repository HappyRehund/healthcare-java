package com.rehund.healthcare.model.appointment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.rehund.healthcare.common.constant.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AppointmentResponse {
    private Long appointmentId;

    private Long patientId;
    private String patientName;

    private Long doctorId;
    private String doctorName;

    private Long doctorSpecializationId;

    private Long hospitalId;
    private String hospitalName;

    private String consultationType;

    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private AppointmentStatus status;
}
