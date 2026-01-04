package com.rehund.healthcare.model.hospitaldoctor;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DoctorResponse {

    private Long doctorId;
    private Long userId;
    private String bio;
    private String name;
    private String email;
    private Long hospitalId;
    private String hospitalName;

    private List<SpecializationInfo> specializations;
    private List<DoctorAvailabilityInfo> doctorAvailabilities;
}
