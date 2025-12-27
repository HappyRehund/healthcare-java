package com.rehund.healthcare.model.hospital;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class HospitalRequest {

    @NotBlank
    @Size(min = 3, max = 255)
    private String name;

    @NotBlank
    @Size(min = 10, max = 255)
    private String address;

    @NotBlank
    @Size(min = 10, max = 100)
    @Pattern(
            regexp = "^[+]?[\\d\\s\\-]{10,15}$",
            message = "Phone number must be between 10 to 15 digits and can include spaces, dashes, and an optional leading +"
    )
    private String phoneNumber;

    @NotBlank
    @Email
    @Size(min = 5, max = 100)
    private String email;

    @NotBlank
    private String description;
}
