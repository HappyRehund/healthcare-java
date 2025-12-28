package com.rehund.healthcare.controller.nonadmin.hospitaldoctor;

import com.rehund.healthcare.entity.hospitaldoctor.Doctor;
import com.rehund.healthcare.model.hospitaldoctor.DoctorAvailabilityRequest;
import com.rehund.healthcare.model.hospitaldoctor.DoctorResponse;
import com.rehund.healthcare.model.user.UserInfo;
import com.rehund.healthcare.service.hospitaldoctor.DoctorService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer")
@RequestMapping("/doctors")
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<Page<DoctorResponse>> searchDoctors(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Sort.Direction direction = sortDirection
                .equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<DoctorResponse> doctorPage = doctorService.getAll(keyword, pageRequest);
        return ResponseEntity.ok(doctorPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getDoctor(
            @PathVariable Long id
    ){
        DoctorResponse response = doctorService.get(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping("/{doctorId}/availabilities")
    public ResponseEntity<DoctorResponse> updateDoctorAvailability(
            @PathVariable Long doctorId,
            @Valid @RequestBody DoctorAvailabilityRequest request
    )
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        Doctor existingDoctor = doctorService.getDoctorByUserId(userInfo.getUserId());

        // ini harusnya di service sih pengecekan apakah doctorId sesuai dengan userId yg login
        if(!existingDoctor.getDoctorId().equals(doctorId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        DoctorResponse response = doctorService.updateDoctorAvailability(doctorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @DeleteMapping("/availabilities/{doctorAvailabilityId}")
    public ResponseEntity<DoctorResponse> deleteDoctorAvailability(
            @PathVariable Long doctorAvailabilityId
    )
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        Doctor existingDoctor = doctorService.getDoctorByUserId(userInfo.getUserId());

        doctorService.deleteDoctorAvailability(existingDoctor.getDoctorId(), doctorAvailabilityId);

        return ResponseEntity.noContent().build();
    }
}
