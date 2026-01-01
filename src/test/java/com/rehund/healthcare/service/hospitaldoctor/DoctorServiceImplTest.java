package com.rehund.healthcare.service.hospitaldoctor;

import com.rehund.healthcare.common.constant.RoleType;
import com.rehund.healthcare.entity.hospitaldoctor.*;
import com.rehund.healthcare.entity.user.Role;
import com.rehund.healthcare.entity.user.User;
import com.rehund.healthcare.model.hospitaldoctor.DoctorRegistrationRequest;
import com.rehund.healthcare.model.hospitaldoctor.DoctorResponse;
import com.rehund.healthcare.model.hospitaldoctor.DoctorSpecializationRequest;
import com.rehund.healthcare.model.user.GrantUserRoleRequest;
import com.rehund.healthcare.repository.hospitaldoctor.*;
import com.rehund.healthcare.repository.user.RoleRepository;
import com.rehund.healthcare.repository.user.UserRepository;
import com.rehund.healthcare.service.cache.CacheService;
import com.rehund.healthcare.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private HospitalRepository hospitalRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private SpecializationRepository specializationRepository;
    @Mock
    private DoctorSpecializationRepository doctorSpecializationRepository;
    @Mock
    private HospitalDoctorFeeRepository hospitalDoctorFeeRepository;
    @Mock
    private DoctorAvailabilityRepository doctorAvailabilityRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private DoctorRegistrationRequest request;
    private GrantUserRoleRequest grantRoleRequest;

    private User user;
    private Hospital hospital;
    private Role doctorRole;
    private Specialization specialization;
    private Doctor doctor;
    private DoctorSpecialization doctorSpecialization;

    @BeforeEach
    void setUp() {

        request = new DoctorRegistrationRequest();
        request.setUserId(1L);
        request.setHospitalId(1L);
        request.setBio("Doctor's bio");
        request.setName("Dr. Test");

        grantRoleRequest = new GrantUserRoleRequest();
        grantRoleRequest.setUserId(1L);
        grantRoleRequest.setRoleType(RoleType.DOCTOR);

        DoctorSpecializationRequest specializationRequest = new DoctorSpecializationRequest();
        specializationRequest.setSpecializationId(1L);
        specializationRequest.setBaseFee(new BigDecimal("100.00"));
        request.setSpecializations(Collections.singletonList(specializationRequest));

        user = new User();
        user.setUserId(1L);
        user.setUsername("doctor");
        user.setEmail("doctor@example.com");

        hospital = new Hospital();
        hospital.setHospitalId(1L);
        hospital.setName("Test Hospital");

        doctorRole = new Role();
        doctorRole.setRoleId(1L);
        doctorRole.setRoleName(RoleType.DOCTOR);

        specialization = new Specialization();
        specialization.setSpecializationId(1L);
        specialization.setName("General Medicine");

        doctor = new Doctor();
        doctor.setDoctorId(1L);
        doctor.setUserId(1L);
        doctor.setHospitalId(1L);
        doctor.setBio("Doctor's bio");
        doctor.setName("Dr. Test");
        doctor.setCreatedAt(LocalDateTime.now());
        doctor.setUpdatedAt(LocalDateTime.now());

        doctorSpecialization = DoctorSpecialization
                .builder()
                .doctorId(doctor.getDoctorId())
                .specializationId(specialization.getSpecializationId())
                .baseFee(BigDecimal.valueOf(100.00))
                .build();
    }

    @Test
    void registerDoctor_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(roleRepository.findByRoleName(RoleType.DOCTOR)).thenReturn(Optional.of(doctorRole));

        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        when(specializationRepository.findById(1L)).thenReturn(Optional.of(specialization));
        when(doctorSpecializationRepository.save(any(DoctorSpecialization.class)))
                .thenReturn(new DoctorSpecialization());
        when(hospitalDoctorFeeRepository.save(any(HospitalDoctorFee.class)))
                .thenReturn(new HospitalDoctorFee());

//        when(doctorSpecializationRepository.findByDoctorId(any()))
//                .thenReturn(List.of(doctorSpecialization));

        DoctorResponse response = doctorService.register(request);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("Dr. Test", response.getName());
        assertEquals("doctor@example.com", response.getEmail());

        assertEquals(1L, response.getHospitalId());
        assertEquals("Test Hospital", response.getHospitalName());
        assertEquals("Doctor's bio", response.getBio());

        assertEquals(new BigDecimal("100.00"), response.getSpecializations().getFirst().getBaseFee());
        assertEquals(1, response.getSpecializations().size());

        verify(userService).grantUserRole(grantRoleRequest);
        verify(roleRepository).findByRoleName(RoleType.DOCTOR);
        verify(doctorRepository).save(any(Doctor.class));
        verify(doctorSpecializationRepository).save(any(DoctorSpecialization.class));
        verify(hospitalDoctorFeeRepository).save(any(HospitalDoctorFee.class));
    }



}