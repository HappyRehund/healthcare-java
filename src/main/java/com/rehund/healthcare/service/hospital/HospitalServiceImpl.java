package com.rehund.healthcare.service.hospital;

import com.rehund.healthcare.common.exception.ResourceNotFoundException;
import com.rehund.healthcare.entity.hospital.Hospital;
import com.rehund.healthcare.model.hospital.HospitalRequest;
import com.rehund.healthcare.model.hospital.HospitalResponse;
import com.rehund.healthcare.repository.hospital.HospitalRepository;
import com.rehund.healthcare.service.cache.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService {

    private final HospitalRepository hospitalRepository;
    private final CacheService cacheService;

    private static String HOSPITAL_CACHE_KEY = "cache:hospital:";

    private Hospital getHospitalById(Long id) {
        String key = HOSPITAL_CACHE_KEY + id;

        return cacheService.get(key, Hospital.class)
                .orElseGet(() -> {
                    return hospitalRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + id));
                });
    }

    @Override
    public Page<HospitalResponse> search(String keyword, Pageable pageable) {
        return null;
    }

    @Override
    public HospitalResponse get(Long id) {
        Hospital hospital = getHospitalById(id);
        return mapHospitalToResponse(hospital);
    }

    @Override
    public HospitalResponse create(HospitalRequest request) {
        // Hospital hospital = new Hospital();
        Hospital hospital = Hospital.builder().build();

        updateHospitalFromRequest(hospital, request);
        Hospital savedHospital = hospitalRepository.save(hospital);
        return mapHospitalToResponse(savedHospital);
    }

    @Override
    public HospitalResponse update(Long id, HospitalRequest request) {
        Hospital hospital = getHospitalById(id);
        updateHospitalFromRequest(hospital, request);
        Hospital updatedHospital = hospitalRepository.save(hospital);
        return mapHospitalToResponse(updatedHospital);
    }

    @Override
    public void delete(Long id) {
        getHospitalById(id);
        hospitalRepository.deleteById(id);
    }

    private HospitalResponse mapHospitalToResponse(Hospital hospital) {
        return HospitalResponse
                .builder()
                .hospitalId(hospital.getHospitalId())
                .name(hospital.getName())
                .email(hospital.getEmail())
                .description(hospital.getDescription())
                .address(hospital.getAddress())
                .phoneNumber(hospital.getPhoneNumber())
                .build();
    }

    private void updateHospitalFromRequest(Hospital hospital, HospitalRequest request) {
        hospital.setName(request.getName());
        hospital.setEmail(request.getEmail());
        hospital.setDescription(request.getDescription());
        hospital.setAddress(request.getAddress());
        hospital.setPhoneNumber(request.getPhoneNumber());
    }
}
