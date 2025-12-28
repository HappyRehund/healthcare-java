package com.rehund.healthcare.service.hospitaldoctor;

import com.rehund.healthcare.common.constant.RoleType;
import com.rehund.healthcare.common.exception.ResourceNotFoundException;
import com.rehund.healthcare.common.exception.user.UserNotFoundException;
import com.rehund.healthcare.entity.hospitaldoctor.*;
import com.rehund.healthcare.entity.user.Role;
import com.rehund.healthcare.entity.user.User;
import com.rehund.healthcare.model.hospitaldoctor.DoctorRegistrationRequest;
import com.rehund.healthcare.model.hospitaldoctor.DoctorResponse;
import com.rehund.healthcare.model.hospitaldoctor.DoctorSpecializationRequest;
import com.rehund.healthcare.model.hospitaldoctor.SpecializationInfo;
import com.rehund.healthcare.model.user.GrantUserRoleRequest;
import com.rehund.healthcare.repository.hospitaldoctor.*;
import com.rehund.healthcare.repository.user.RoleRepository;
import com.rehund.healthcare.repository.user.UserRepository;
import com.rehund.healthcare.service.cache.CacheService;
import com.rehund.healthcare.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorServiceImpl implements DoctorService {

    private final UserService userService;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HospitalRepository hospitalRepository;
    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;
    private final DoctorSpecializationRepository doctorSpecializationRepository;
    private final HospitalDoctorFeeRepository hospitalDoctorFeeRepository;

    private final CacheService cacheService;

    public static final String DOCTOR_CACHE_KEY = "cache:doctor:";
    private static final Duration DOCTOR_CACHE_TTL = Duration.ofHours(1);

    private Doctor getDoctorById(Long doctorId) {
        String key = DOCTOR_CACHE_KEY + doctorId;

        return cacheService.getOrLoad(
                key,
                Doctor.class,
                DOCTOR_CACHE_TTL,
                () -> doctorRepository.findById(doctorId)
                        .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + doctorId))
        );
    }

    @Override
    @Transactional
    public DoctorResponse register(DoctorRegistrationRequest request) {
        log.info("Registering new doctor with user Id {}", request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User with Id " + request.getUserId() + " not found"));

        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital with Id " + request.getHospitalId() + " not found"));

        Role doctorRole = roleRepository.findByRoleName(RoleType.DOCTOR)
                .orElseThrow(() -> new ResourceNotFoundException("Role DOCTOR not found"));

        // grant doctor role to user

        userService.grantUserRole(
                GrantUserRoleRequest
                        .builder()
                        .userId(user.getUserId())
                        .roleType(RoleType.DOCTOR)
                        .build()
        );

        Doctor doctor = Doctor
                .builder()
                .userId(request.getUserId())
                .hospitalId(request.getHospitalId())
                .name(request.getName())
                .bio(request.getBio())
                .build();

        doctorRepository.save(doctor);

        // process specialization
        List<SpecializationInfo> specializationInfoList = new ArrayList<>();

        for (DoctorSpecializationRequest specializationRequest : request.getSpecializations()){

            Specialization specialization = specializationRepository.findById(specializationRequest.getSpecializationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Specialization with Id " + specializationRequest.getSpecializationId() + " not found"));

            DoctorSpecialization doctorSpecialization = DoctorSpecialization
                    .builder()
                    .doctorId(doctor.getDoctorId())
                    .specializationId(specializationRequest.getSpecializationId())
                    .baseFee(specializationRequest.getBaseFee())
                    .build();

            doctorSpecializationRepository.save(doctorSpecialization);

            HospitalDoctorFee hospitalDoctorFee = HospitalDoctorFee
                    .builder()
                    .doctorSpecializationId(doctorSpecialization.getDoctorSpecializationId())
                    .hospitalId(request.getHospitalId())
                    .fee(specializationRequest.getBaseFee()) // bisa diupdate nanti
                    .build();

            hospitalDoctorFeeRepository.save(hospitalDoctorFee);

            specializationInfoList.add(
                    SpecializationInfo
                            .builder()
                            .specializationId(specialization.getSpecializationId())
                            .specializationName(specialization.getName())
                            .baseFee(doctorSpecialization.getBaseFee())
                            .hospitalFee(hospitalDoctorFee.getFee())
                            .build()
            );
        }

        log.info("Doctor registered with Id {}", doctor.getDoctorId());

        return DoctorResponse
                .builder()
                .doctorId(doctor.getDoctorId())
                .bio(doctor.getBio())
                .userId(user.getUserId())
                .email(user.getEmail())
                .hospitalId(hospital.getHospitalId())
                .hospitalName(hospital.getName())
                .specializations(specializationInfoList)
                .availabilities(new ArrayList<>()) // nanti diisi, ketika di-register masih kosong
                .build();
    }

    @Override
    public Page<DoctorResponse> getAll(String keyword, Pageable pageable) {
        return doctorRepository.searchDoctors(keyword, pageable)
                .map(doctor -> get(doctor.getDoctorId()));
    }

    @Override
    public DoctorResponse get(Long doctorId) {
        Doctor doctor = getDoctorById(doctorId);
        return mapDoctorToDoctorResponse(doctor);
    }

    private DoctorResponse mapDoctorToDoctorResponse(Doctor doctor){

        User user = userRepository.findById(doctor.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User with Id " + doctor.getUserId() + " not found"));

        Hospital hospital = hospitalRepository.findById(doctor.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital with Id " + doctor.getHospitalId() + " not found"));

        List<DoctorSpecialization> specializationList = doctorSpecializationRepository.findByDoctorId(doctor.getDoctorId());

        List<SpecializationInfo> specializationInfoList = specializationList.stream()
                .map(specialization -> {
                    String specializationName = specializationRepository.findById(specialization.getSpecializationId())
                            .map(Specialization::getName)
                            .orElse("Unknown Specialization");

                    HospitalDoctorFee hospitalDoctorFee = hospitalDoctorFeeRepository
                            .findByHospitalIdAndDoctorSpecializationId(hospital.getHospitalId(), specialization.getDoctorSpecializationId())
                            .orElseThrow(() -> new ResourceNotFoundException("HospitalDoctorFee not found for hospital Id " + hospital.getHospitalId() + " and specialization Id " + specialization.getDoctorSpecializationId()));

                    return SpecializationInfo
                            .builder()
                            .specializationId(specialization.getSpecializationId())
                            .specializationName(specializationName)
                            .baseFee(specialization.getBaseFee())
                            .hospitalFee(hospitalDoctorFee.getFee())
                            .build();
                }).toList();

        return DoctorResponse
                .builder()
                .doctorId(doctor.getDoctorId())
                .bio(doctor.getBio())
                .userId(user.getUserId())
                .email(user.getEmail())
                .hospitalId(hospital.getHospitalId())
                .hospitalName(hospital.getName())
                .specializations(specializationInfoList)
                .build();
    }
}
