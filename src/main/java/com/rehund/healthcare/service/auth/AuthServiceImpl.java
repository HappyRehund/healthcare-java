package com.rehund.healthcare.service.auth;

import com.rehund.healthcare.common.exception.user.InvalidPasswordException;
import com.rehund.healthcare.model.auth.AuthRequest;
import com.rehund.healthcare.model.user.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;


    @Override
    public UserInfo authenticate(AuthRequest authRequest) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            return (UserInfo) authentication.getPrincipal();

        } catch (Exception e) {
            log.error("Authentication failed for user: {}", authRequest.getUsername(), e);
            throw new InvalidPasswordException("Invalid username or password");
        }

    }
}
