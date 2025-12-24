package com.rehund.healthcare.controller.auth;

import com.rehund.healthcare.model.user.UserRegisterRequest;
import com.rehund.healthcare.model.user.UserResponse;
import com.rehund.healthcare.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody UserRegisterRequest request
            )
    {
        UserResponse response = userService.registerUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
