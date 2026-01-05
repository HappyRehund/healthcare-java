package com.rehund.healthcare.model.hospitaldoctor;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DoctorSpecializationRequest {

    @NotNull
    @Positive
    private Long specializationId;

    @NotNull
    @Positive
    private BigDecimal fee;

    @NotNull
    @Pattern(regexp = "^(ONLINE|OFFLINE)$", message = "consultation type must be either online or offline")
    private String consultationType;
}
