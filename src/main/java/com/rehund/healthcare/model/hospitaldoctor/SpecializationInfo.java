package com.rehund.healthcare.model.hospitaldoctor;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
public class SpecializationInfo {
    private Long specializationId;
    private String specializationName;
    private String description;

    private BigDecimal baseFee;
    private BigDecimal hospitalFee;

    private String consultationType;
}
