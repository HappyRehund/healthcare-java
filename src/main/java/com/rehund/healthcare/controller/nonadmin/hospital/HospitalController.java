package com.rehund.healthcare.controller.nonadmin.hospital;

import com.rehund.healthcare.model.hospital.HospitalResponse;
import com.rehund.healthcare.service.hospital.HospitalService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer")
@RequestMapping("/hospitals")
public class HospitalController {

    private final HospitalService hospitalService;

    @GetMapping("/search")
    public ResponseEntity<Page<HospitalResponse>> searchHospitals(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    )
    {
        Sort.Direction direction = sortDirection
                .equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<HospitalResponse> hospitalPage = hospitalService.search(keyword, pageRequest);

        return ResponseEntity.ok(hospitalPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HospitalResponse> getHospital(
            @PathVariable Long id
    )
    {
        HospitalResponse response = hospitalService.get(id);
        return ResponseEntity.ok(response);
    }


}
