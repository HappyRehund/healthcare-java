package com.rehund.healthcare.service.hospitaldoctor;

import com.rehund.healthcare.entity.hospitaldoctor.Doctor;
import com.rehund.healthcare.entity.hospitaldoctor.DoctorAvailability;
import com.rehund.healthcare.model.hospitaldoctor.DoctorAvailabilityRequest;
import com.rehund.healthcare.model.hospitaldoctor.DoctorRegistrationRequest;
import com.rehund.healthcare.model.hospitaldoctor.DoctorResponse;
import com.rehund.healthcare.model.hospitaldoctor.DoctorSpecializationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface DoctorService {

    Page<DoctorResponse> getAll(String keyword, Pageable pageable);
    DoctorResponse register(DoctorRegistrationRequest request);
    DoctorResponse get(Long doctorId);
    DoctorResponse addDoctorSpecializations(Long doctorId, DoctorSpecializationRequest request);
    DoctorResponse updateDoctorAvailability(Long doctorId, DoctorAvailabilityRequest request);

    Doctor getDoctorByUserId(Long userId);

    void deleteDoctorAvailability(Long doctorId, Long doctorAvailabilityId);
    List<DoctorAvailability> getDoctorAvailabilitiesFromToday(Long doctorId);


}
