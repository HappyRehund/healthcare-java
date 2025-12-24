package com.rehund.healthcare.model.user;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.rehund.healthcare.common.constant.RoleType;
import com.rehund.healthcare.entity.user.Role;
import com.rehund.healthcare.entity.user.User;
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
public class UserResponse {

    private Long userId;
    private String username;
    private String email;
    private boolean enabled;
    private List<RoleType> roles;

    public static UserResponse fromUserAndRoles(User user, List<Role> roles) {

        List<RoleType> roleTypes = roles.stream()
                .map(Role::getRoleName)
                .toList();

        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .roles(roleTypes)
                .build();
    }
}
