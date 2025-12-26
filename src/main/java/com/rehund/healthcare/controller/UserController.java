package com.rehund.healthcare.controller;

import com.rehund.healthcare.common.exception.ForbiddenAccessException;
import com.rehund.healthcare.model.user.UserInfo;
import com.rehund.healthcare.model.user.UserResponse;
import com.rehund.healthcare.model.user.UserUpdateRequest;
import com.rehund.healthcare.service.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@SecurityRequirement(name = "Bearer")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        UserResponse userResponse = UserResponse.fromUserAndRoles(userInfo.getUser(), userInfo.getRoles());

        return ResponseEntity.ok(userResponse);

    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request
            ){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        if (!Objects.equals(userInfo.getUser().getUserId(), id)){
            throw new ForbiddenAccessException("You are not allowed to update this user");
        }

        UserResponse updatedUser = userService.updateUser(id, request);

        return ResponseEntity.ok(updatedUser);
    }
}
