package com.rehund.healthcare.controller.admin.hospital;

import com.rehund.healthcare.model.hospital.HospitalRequest;
import com.rehund.healthcare.model.hospital.HospitalResponse;
import com.rehund.healthcare.service.hospital.HospitalService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/admin/hospitals")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminHospitalController {

    private final HospitalService hospitalService;

    @PostMapping
    public ResponseEntity<HospitalResponse> registerHospital(
            @Valid @RequestBody HospitalRequest request
            )
    {
        HospitalResponse hospitalResponse = hospitalService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(hospitalResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HospitalResponse> updateHospital(
            @PathVariable Long id,
            @Valid @RequestBody HospitalRequest request
    )
    {
        HospitalResponse hospitalResponse = hospitalService.update(id,  request);
        return ResponseEntity.ok(hospitalResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHospital(
            @PathVariable Long id
    )
    {
        hospitalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
