package com.rehund.healthcare.model.auth;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.rehund.healthcare.common.constant.RoleType;
import com.rehund.healthcare.model.user.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuthResponse {
    private String token;
    private Long userId;
    private String username;
    private String email;
    private List<RoleType> roles;

    public static AuthResponse fromUserInfoAndToken(UserInfo userInfo, String token){
        return AuthResponse.builder()
                .token(token)
                .userId(userInfo.getUser().getUserId())
                .username(userInfo.getUser().getUsername())
                .email(userInfo.getUser().getEmail())
                .roles(
                        userInfo.getRoles()
                                .stream()
                                .map(role -> role.getRoleName())
                                .toList()
                )
                .build();
    }
}
