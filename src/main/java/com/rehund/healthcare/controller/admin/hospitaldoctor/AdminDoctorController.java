package com.rehund.healthcare.controller.admin.hospitaldoctor;

import com.rehund.healthcare.entity.hospitaldoctor.Doctor;
import com.rehund.healthcare.model.hospitaldoctor.DoctorAvailabilityRequest;
import com.rehund.healthcare.model.hospitaldoctor.DoctorRegistrationRequest;
import com.rehund.healthcare.model.hospitaldoctor.DoctorResponse;
import com.rehund.healthcare.model.hospitaldoctor.DoctorSpecializationRequest;
import com.rehund.healthcare.model.user.UserInfo;
import com.rehund.healthcare.service.hospitaldoctor.DoctorService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/admin/doctors")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HOSPITAL_ADMIN')")
public class AdminDoctorController {

    private final DoctorService doctorService;

    @PostMapping("/register")
    public ResponseEntity<DoctorResponse> registerDoctor(
            @Valid @RequestBody DoctorRegistrationRequest request
    ){
        DoctorResponse response = doctorService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{doctorId}/specializations")
    public ResponseEntity<DoctorResponse> addDoctorSpecialization(
            @PathVariable Long doctorId,
            @Valid @RequestBody DoctorSpecializationRequest request
            ){
        DoctorResponse response = doctorService.addDoctorSpecializations(doctorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
