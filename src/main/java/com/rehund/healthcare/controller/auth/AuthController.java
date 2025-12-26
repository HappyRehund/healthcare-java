package com.rehund.healthcare.controller.auth;

import com.rehund.healthcare.model.auth.AuthRequest;
import com.rehund.healthcare.model.auth.AuthResponse;
import com.rehund.healthcare.model.user.UserInfo;
import com.rehund.healthcare.model.user.UserRegisterRequest;
import com.rehund.healthcare.model.user.UserResponse;
import com.rehund.healthcare.service.auth.AuthService;
import com.rehund.healthcare.service.auth.JwtService;
import com.rehund.healthcare.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody UserRegisterRequest request
            )
    {
        UserResponse response = userService.registerUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(
            @Valid @RequestBody AuthRequest authRequest
            )
    {
        UserInfo userInfo = authService.authenticate(authRequest);
        String token = jwtService.generateToken(userInfo);

        AuthResponse authResponse = AuthResponse.fromUserInfoAndToken(userInfo, token);

        return ResponseEntity.ok(authResponse);
    }
}
