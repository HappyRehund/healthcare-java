package com.rehund.healthcare.service.hospital;

import com.rehund.healthcare.model.hospital.HospitalRequest;
import com.rehund.healthcare.model.hospital.HospitalResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HospitalService {
    Page<HospitalResponse> search(String keyword, Pageable pageable);
    HospitalResponse get(Long id);
    HospitalResponse create(HospitalRequest request);
    HospitalResponse update(Long id, HospitalRequest request);
    void delete(Long id);
}
