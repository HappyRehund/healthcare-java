package com.rehund.healthcare.service.hospitaldoctor;

import com.rehund.healthcare.common.constant.RoleType;
import com.rehund.healthcare.common.exception.ForbiddenAccessException;
import com.rehund.healthcare.common.exception.ResourceNotFoundException;
import com.rehund.healthcare.common.exception.user.UserNotFoundException;
import com.rehund.healthcare.entity.hospitaldoctor.*;
import com.rehund.healthcare.entity.user.Role;
import com.rehund.healthcare.entity.user.User;
import com.rehund.healthcare.model.hospitaldoctor.*;
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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;

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
                        .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId))
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
                        .roleType(doctorRole.getRoleName())
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

            // check if doctor specialization already exists if not create new
            DoctorSpecialization doctorSpecialization = doctorSpecializationRepository
                    .findByDoctorIdAndSpecializationId(doctor.getDoctorId(), specialization.getSpecializationId())
                            .orElseGet(() -> {
                                DoctorSpecialization newDoctorSpecialization = DoctorSpecialization
                                        .builder()
                                        .doctorId(doctor.getDoctorId())
                                        .specializationId(specializationRequest.getSpecializationId())
                                        .build();

                                return doctorSpecializationRepository.save(newDoctorSpecialization);
                            });

            HospitalDoctorFee hospitalDoctorFee = HospitalDoctorFee
                    .builder()
                    .hospitalId(request.getHospitalId())
                    .doctorSpecializationId(doctorSpecialization.getDoctorSpecializationId())
                    .fee(specializationRequest.getFee())
                    .consultationType(specializationRequest.getConsultationType())
                    .build();

            hospitalDoctorFeeRepository.save(hospitalDoctorFee);

            specializationInfoList.add(
                    SpecializationInfo
                            .builder()
                            .specializationId(specialization.getSpecializationId())
                            .specializationName(specialization.getName())
                            .description(specialization.getDescription())
                            .fee(hospitalDoctorFee.getFee())
                            .consultationType(hospitalDoctorFee.getConsultationType())
                            .build()
            );
        }

        log.info("Doctor registered with Id {}", doctor.getDoctorId());

        return DoctorResponse
                .builder()
                .doctorId(doctor.getDoctorId())
                .bio(doctor.getBio())
                .name(doctor.getName())
                .userId(user.getUserId())
                .email(user.getEmail())
                .hospitalId(hospital.getHospitalId())
                .hospitalName(hospital.getName())
                .specializations(specializationInfoList)
                .doctorAvailabilities(new ArrayList<>()) // nanti diisi, ketika di-register masih kosong
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

    @Override
    @Transactional
    public DoctorResponse addDoctorSpecializations(
            Long doctorId,
            DoctorSpecializationRequest request
    )
    {
        Doctor doctor = getDoctorById(doctorId);

        specializationRepository.findById(request.getSpecializationId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialization with Id " + request.getSpecializationId() + " not found"));

        // Check if doctor specialization already exists if not create new
        DoctorSpecialization doctorSpecialization = doctorSpecializationRepository
                .findByDoctorIdAndSpecializationId(doctor.getDoctorId(), request.getSpecializationId())
                .orElseGet(() -> {;
                    DoctorSpecialization newDoctorSpecialization = DoctorSpecialization
                            .builder()
                            .doctorId(doctor.getDoctorId())
                            .specializationId(request.getSpecializationId())
                            .build();

                    return doctorSpecializationRepository.save(newDoctorSpecialization);
                });

        boolean feeExists = hospitalDoctorFeeRepository.existsByHospitalIdAndDoctorSpecializationIdAndConsultationType(
                doctor.getHospitalId(),
                doctorSpecialization.getDoctorSpecializationId(),
                request.getConsultationType()
        );
        if (feeExists) {
            throw new ForbiddenAccessException("Hospital Doctor Fee already exists for hospital Id " + doctor.getHospitalId() + " and specialization Id " + request.getSpecializationId());
        }

        HospitalDoctorFee hospitalDoctorFee = HospitalDoctorFee
                .builder()
                .doctorSpecializationId(doctorSpecialization.getDoctorSpecializationId())
                .hospitalId(doctor.getHospitalId())
                .fee(request.getFee()) // bisa diupdate nanti
                .consultationType(request.getConsultationType())
                .build();

        hospitalDoctorFeeRepository.save(hospitalDoctorFee);

        return mapDoctorToDoctorResponse(doctor);
    }

    @Override
    @Transactional
    public DoctorResponse updateDoctorAvailability(Long doctorId, DoctorAvailabilityRequest request) {
        String key = DOCTOR_CACHE_KEY + doctorId;
        // find the doctor
        Doctor doctor = getDoctorById(doctorId);

        DoctorAvailability doctorAvailability = DoctorAvailability
                .builder()
                .doctorId(doctor.getDoctorId())
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .consultationType(request.getConsultationType())
                .is_available(true)
                .build();

        doctorAvailabilityRepository.save(doctorAvailability);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                cacheService.evict(key);
            }
        });

        return mapDoctorToDoctorResponse(doctor);
    }

    @Override
    public void deleteDoctorAvailability(Long doctorId, Long doctorAvailabilityId) {
        Doctor doctor = getDoctorById(doctorId);

        DoctorAvailability doctorAvailability = doctorAvailabilityRepository.findById(doctorAvailabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor Availability with Id " + doctorAvailabilityId + " not found"));

        if (!doctorAvailability.getDoctorId().equals(doctor.getDoctorId())){
            throw new ForbiddenAccessException("Doctor Availability with Id " + doctorAvailabilityId + " does not belong to Doctor with Id " + doctorId);
        }

        doctorAvailabilityRepository.deleteById(doctorAvailabilityId);
    }

    @Override
    public List<DoctorAvailability> getDoctorAvailabilitiesFromToday(Long doctorId) {
        return doctorAvailabilityRepository.findDoctorAvailabilitiesByDoctorIdFromToday(doctorId);
    }

    @Override
    public Doctor getDoctorByUserId(Long userId) {
        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor with User Id " + userId + " not found"));
    }

    private DoctorResponse mapDoctorToDoctorResponse(Doctor doctor){

        User user = userRepository.findById(doctor.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User with Id " + doctor.getUserId() + " not found"));

        Hospital hospital = hospitalRepository.findById(doctor.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital with Id " + doctor.getHospitalId() + " not found"));

        List<DoctorSpecialization> specializationList = doctorSpecializationRepository.findByDoctorId(doctor.getDoctorId());

        List<SpecializationInfo> specializationInfoList = specializationList.stream()
                .flatMap(doctorSpecialization -> {
                     Specialization specialization = specializationRepository.findById(doctorSpecialization.getSpecializationId())
                             .orElseThrow(() -> new ResourceNotFoundException("Specialization with Id " + doctorSpecialization.getSpecializationId() + " not found"));

                    List<HospitalDoctorFee> hospitalDoctorFeeList = hospitalDoctorFeeRepository
                            .findAllByHospitalIdAndDoctorSpecializationId(hospital.getHospitalId(), doctorSpecialization.getDoctorSpecializationId());

                    return hospitalDoctorFeeList.stream()
                            .map(fee -> SpecializationInfo
                                    .builder()
                                    .specializationId(doctorSpecialization.getSpecializationId())
                                    .specializationName(specialization.getName())
                                    .description(specialization.getDescription())
                                    .fee(fee.getFee())
                                    .consultationType(fee.getConsultationType())
                                    .build()
                            );
                }).toList();

        List<DoctorAvailabilityInfo> doctorAvailabilityInfoList = getDoctorAvailabilitiesFromToday(doctor.getDoctorId())
                .stream()
                .map(doctorAvailability -> DoctorAvailabilityInfo
                        .builder()
                        .doctorAvailabilityId(doctorAvailability.getDoctorAvailabilityId())
                        .isAvailable(true)
                        .startDateTime(LocalDateTime.of(doctorAvailability.getDate(), doctorAvailability.getStartTime()))
                        .endDateTime(LocalDateTime.of(doctorAvailability.getDate(), doctorAvailability.getEndTime()))
                        .consultationType(doctorAvailability.getConsultationType())
                        .build())
                .toList();

        return DoctorResponse
                .builder()
                .doctorId(doctor.getDoctorId())
                .name(doctor.getName())
                .bio(doctor.getBio())
                .userId(user.getUserId())
                .email(user.getEmail())
                .hospitalId(hospital.getHospitalId())
                .hospitalName(hospital.getName())
                .specializations(specializationInfoList)
                .doctorAvailabilities(doctorAvailabilityInfoList)
                .build();
    }
}
