package com.rehund.healthcare.service.user;

import com.rehund.healthcare.model.user.UserRegisterRequest;
import com.rehund.healthcare.model.user.UserResponse;
import com.rehund.healthcare.model.user.UserUpdateRequest;

public interface UserService {

    UserResponse registerUser(UserRegisterRequest request);

    UserResponse getUserById(Long userId);

    UserResponse getUserByUsername(String username);

    UserResponse updateUser(Long userId, UserUpdateRequest request);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
