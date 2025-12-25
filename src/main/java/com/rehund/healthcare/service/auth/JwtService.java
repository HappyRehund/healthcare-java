package com.rehund.healthcare.service.auth;

import com.rehund.healthcare.model.user.UserInfo;

public interface JwtService {
    String generateToken(UserInfo userInfo);

    boolean validateToken(String token);

    String getUsernameFromToken(String token);
}
