package com.rehund.healthcare.service.hospital;

import com.rehund.healthcare.entity.hospital.Hospital;
import com.rehund.healthcare.model.hospital.HospitalRequest;
import com.rehund.healthcare.model.hospital.HospitalResponse;
import com.rehund.healthcare.repository.hospital.HospitalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HospitalServiceImplTest {

    @Mock
    private HospitalRepository hospitalRepository;

    @InjectMocks
    private HospitalServiceImpl hospitalService;

    private HospitalRequest hospitalRequest;
    private Hospital hospital;
    private HospitalResponse hospitalResponse;

    @BeforeEach
    void setup(){
        hospitalRequest = HospitalRequest
                .builder()
                .name("Test Hospital")
                .address("123 Test St")
                .phoneNumber("12345678901")
                .email("test@hospital.com")
                .build();

        hospital = Hospital
                .builder()
                .hospitalId(1L)
                .name("Test Hospital")
                .address("123 Test St")
                .phoneNumber("12345678901")
                .email("test@hospital.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        hospitalResponse = HospitalResponse
                .builder()
                .hospitalId(1L)
                .name("Test Hospital")
                .address("123 Test St")
                .phoneNumber("12345678901")
                .email("test@hospital.com")
                .build();
    }

    @Test
    void registerHospital_shouldReturnHospitalResponse(){
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(hospital);

        HospitalResponse result = hospitalService.create(hospitalRequest);

        assertNotNull(result);
        assertEquals(hospitalResponse.getName(), result.getName());
        assertEquals(hospitalResponse.getAddress(), result.getAddress());
        assertEquals(hospitalResponse.getEmail(), result.getEmail());
        assertEquals(hospitalResponse.getPhoneNumber(), result.getPhoneNumber());

        // fungsi ini memastikan bahwa method save pada hospitalRepository dipanggil tepat satu kali
        verify(hospitalRepository, times(1)).save(any(Hospital.class));
    }
}