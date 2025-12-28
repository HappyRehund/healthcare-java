package com.rehund.healthcare.service.hospitaldoctor;

import com.rehund.healthcare.model.hospitaldoctor.DoctorRegistrationRequest;
import com.rehund.healthcare.model.hospitaldoctor.DoctorResponse;

import java.util.List;

public interface DoctorService {

    DoctorResponse register(DoctorRegistrationRequest request);
    List<DoctorResponse> getAll();
    DoctorResponse get(Long doctorId);

}
