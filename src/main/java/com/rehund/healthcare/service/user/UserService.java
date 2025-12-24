package com.rehund.healthcare.service.user;

import com.rehund.healthcare.model.user.UserRegisterRequest;
import com.rehund.healthcare.model.user.UserResponse;

public interface UserService {

    UserResponse registerUser(UserRegisterRequest request);

    UserResponse getUserById(Long userId);

    UserResponse getUserByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
