package com.rehund.healthcare.model.user;

import jakarta.validation.constraints.Email;
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
public class UserUpdateRequest {

    @Size(min = 3, max = 50)
    private String username;

    @Size(max = 100)
    @Email
    private String email;

    @Size(min = 8, max = 255)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
            message = "Password must contain at least one uppercase, one lowercase, one digit, and one special character (@$!%*?&)"
    )
    private String newPassword;

    @Size(min = 8, max = 255)
    private String currentPassword;
}
