package com.rehund.healthcare.service.auth;

import com.rehund.healthcare.common.constant.RoleType;
import com.rehund.healthcare.common.exception.user.InvalidPasswordException;
import com.rehund.healthcare.entity.user.Role;
import com.rehund.healthcare.entity.user.User;
import com.rehund.healthcare.model.auth.AuthRequest;
import com.rehund.healthcare.model.user.UserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private AuthRequest request;
    private UserInfo userInfo;

    @BeforeEach
    void setup(){
        request = new AuthRequest("testUser", "testPassword123%");
        userInfo = new UserInfo(
                User.builder()
                        .username("testUser")
                        .build(),
                List.of(Role.builder()
                        .roleName(RoleType.PATIENT)
                        .build()
                )
        );
    }

    @Test
    void authenticate_SuccessfulAuthentication_ReturnsUserInfo() {

        Authentication authentication = new UsernamePasswordAuthenticationToken(userInfo, null);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        UserInfo result = authService.authenticate(request);

        assertNotNull(result);
        assertEquals(userInfo.getUsername(), result.getUsername());
        assertNotEquals("wrongUser", userInfo.getUsername());
    }

    @Test
    void authenticate_FailedAuthentication_ThrowsException(){

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Authentication failed"));

        assertThrows(InvalidPasswordException.class, () -> authService.authenticate(request));

    }
}