package com.rehund.healthcare.controller.admin.user;

import com.rehund.healthcare.model.user.GrantUserRoleRequest;
import com.rehund.healthcare.model.user.UserResponse;
import com.rehund.healthcare.service.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminUserController {
    private final UserService userService;

    @PostMapping("/grant")
    public ResponseEntity<UserResponse> grant(
            @Valid @RequestBody GrantUserRoleRequest request
            )
    {
        UserResponse response = userService.grantUserRole(request);
        return ResponseEntity.ok(response);
    }

}
