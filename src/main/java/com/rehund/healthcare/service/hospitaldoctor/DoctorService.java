package com.rehund.healthcare.service.hospitaldoctor;

import com.rehund.healthcare.model.hospitaldoctor.DoctorRegistrationRequest;
import com.rehund.healthcare.model.hospitaldoctor.DoctorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DoctorService {

    DoctorResponse register(DoctorRegistrationRequest request);
    Page<DoctorResponse> getAll(String keyword, Pageable pageable);
    DoctorResponse get(Long doctorId);

}
