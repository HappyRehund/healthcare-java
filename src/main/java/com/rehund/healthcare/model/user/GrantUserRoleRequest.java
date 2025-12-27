package com.rehund.healthcare.model.user;

import com.rehund.healthcare.common.constant.RoleType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GrantUserRoleRequest {

    @NotBlank
    private Long userId;

    @NotBlank
    private RoleType roleType;
}
