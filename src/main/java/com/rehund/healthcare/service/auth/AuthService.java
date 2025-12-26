package com.rehund.healthcare.service.auth;

import com.rehund.healthcare.model.auth.AuthRequest;
import com.rehund.healthcare.model.user.UserInfo;

public interface AuthService {
    UserInfo authenticate(AuthRequest authRequest);
}
