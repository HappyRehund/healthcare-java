package com.rehund.healthcare.service.user;

import com.rehund.healthcare.common.constant.RoleType;
import com.rehund.healthcare.model.user.GrantUserRoleRequest;
import com.rehund.healthcare.model.user.UserRegisterRequest;
import com.rehund.healthcare.model.user.UserResponse;
import com.rehund.healthcare.model.user.UserUpdateRequest;

public interface UserService {

    UserResponse registerUser(UserRegisterRequest request);

    UserResponse getUserById(Long userId);

    UserResponse getUserByUsername(String username);

    UserResponse updateUser(Long userId, UserUpdateRequest request);

    UserResponse grantUserRole(GrantUserRoleRequest request);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
