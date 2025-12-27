package com.rehund.healthcare.model.user;

import com.rehund.healthcare.common.constant.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GrantUserRoleRequest {

    @NotNull
    private Long userId;

    @NotNull
    private RoleType roleType;
}
