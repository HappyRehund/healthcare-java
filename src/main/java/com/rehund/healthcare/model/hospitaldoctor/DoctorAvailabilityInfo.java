package com.rehund.healthcare.model.hospitaldoctor;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)

public class DoctorAvailabilityInfo {
    private Long doctorAvailabilityId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String consultationType;
    private boolean isAvailable;
}
