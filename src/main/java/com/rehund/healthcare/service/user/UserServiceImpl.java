package com.rehund.healthcare.service.user;

import com.rehund.healthcare.common.constant.RoleType;
import com.rehund.healthcare.common.exception.user.*;
import com.rehund.healthcare.entity.user.Role;
import com.rehund.healthcare.entity.user.User;
import com.rehund.healthcare.entity.user.UserRole;
import com.rehund.healthcare.model.user.UserRegisterRequest;
import com.rehund.healthcare.model.user.UserResponse;
import com.rehund.healthcare.model.user.UserUpdateRequest;
import com.rehund.healthcare.repository.user.RoleRepository;
import com.rehund.healthcare.repository.user.UserRepository;
import com.rehund.healthcare.repository.user.UserRoleRepository;
import com.rehund.healthcare.service.cache.CacheService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    private final CacheService cacheService;

    private final String USER_CACHE_KEY = "cache:user:";
    private final String USER_ROLES_CACHE_KEY = "cache:user:roles:";

    @Override
    @Transactional
    public UserResponse registerUser(UserRegisterRequest request) {
        if(existsByUsername(request.getUsername())){
            throw new UsernameConflictException("Username " + request.getUsername() + " is already taken");
        }

        if(existsByEmail(request.getEmail())){
            throw new EmailConflictException("Email " + request.getEmail() + " is already taken");
        }

        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new IllegalArgumentException("Password and Confirm Password do not match");
        }

        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .build();

        userRepository.save(newUser);

        log.info("Registered new user with username: {}", newUser.getUsername());

        Role role = roleRepository.findByRoleName(RoleType.PATIENT)
                .orElseThrow(() -> new RoleNotFoundException("Role with role name " + RoleType.PATIENT + "  not found"));

        UserRole newUserRole = UserRole.builder()
                .id(new UserRole.UserRoleId(newUser.getUserId(), role.getRoleId()))
                .build();

        userRoleRepository.save(newUserRole);

        return UserResponse.fromUserAndRoles(newUser, List.of(role));
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));

        List<Role> roles = roleRepository.findByUserId(userId);
        return UserResponse.fromUserAndRoles(user, roles);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));

        List<Role> roles = roleRepository.findByUserId(user.getUserId());
        return UserResponse.fromUserAndRoles(user, roles);
    }

    @Override
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {

        // fetch existing user by id
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));

        // if update password, check password update and current password
        if (
                request.getNewPassword() != null
                && request.getCurrentPassword() != null
                && !request.getCurrentPassword().equals(request.getNewPassword())
        ){
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
                throw new InvalidPasswordException("Current password is incorrect");
            }

            user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        }

        // if update email, check email conflict then update

        if (
                request.getEmail() != null
                && !request.getEmail().equals(user.getEmail())
        ) {
            if (existsByEmail(request.getEmail())) {
                throw new EmailConflictException("Email " + request.getEmail() + " is already taken");
            }
            user.setEmail(request.getEmail());
        }

        // if update username, check username conflict then update
        String oldUsername = user.getUsername();
        if (
                request.getUsername() != null
                && !request.getUsername().equals(user.getUsername())
        ) {
            if (existsByUsername(request.getUsername())) {
                throw new UsernameConflictException("Username " + request.getUsername() + " is already taken");
            }
            // get the old username before saving
            user.setUsername(request.getUsername());

        }

        // save updated user
        userRepository.save(user);

        // get the roles of the user by user id
        List<Role> roles = roleRepository.findByUserId(userId);

        // evict cache
        cacheService.evict(USER_CACHE_KEY + oldUsername);
        cacheService.evict(USER_ROLES_CACHE_KEY + oldUsername);

        // If the username changed, also evict cache for the new username
        if (!oldUsername.equals(user.getUsername())) {
            cacheService.evict(USER_CACHE_KEY + user.getUsername());
            cacheService.evict(USER_ROLES_CACHE_KEY + user.getUsername());
        }

        // return UserResponse
        return UserResponse.fromUserAndRoles(user, roles);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
