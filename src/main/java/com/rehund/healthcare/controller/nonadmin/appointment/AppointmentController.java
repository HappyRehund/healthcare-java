package com.rehund.healthcare.controller.nonadmin.appointment;

import com.rehund.healthcare.model.appointment.AppointmentBookRequest;
import com.rehund.healthcare.model.appointment.AppointmentRescheduleRequest;
import com.rehund.healthcare.model.appointment.AppointmentResponse;
import com.rehund.healthcare.model.user.UserInfo;
import com.rehund.healthcare.service.appointment.AppointmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer")
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>>  getUserAppointments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        List<AppointmentResponse> responses = appointmentService.listUserAppointments(userInfo.getUserId());

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/book")
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @Valid @RequestBody AppointmentBookRequest request
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        AppointmentResponse response = appointmentService.bookAppointment(userInfo.getUserId(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{appointmentId}/reschedule")
    public ResponseEntity<AppointmentResponse> rescheduleAppointment(
            @PathVariable Long appointmentId,
            @Valid @RequestBody AppointmentRescheduleRequest request
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        AppointmentResponse response = appointmentService.rescheduleAppointment(
                userInfo.getUserId(),
                appointmentId,
                request
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable Long appointmentId
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        appointmentService.cancelAppointment(
                userInfo.getUserId(),
                appointmentId
        );

        return ResponseEntity.noContent().build();
    }


}
