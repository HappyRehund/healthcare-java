package com.rehund.healthcare.service.appointment;

import com.rehund.healthcare.common.constant.AppointmentStatus;
import com.rehund.healthcare.common.constant.RoleType;
import com.rehund.healthcare.common.exception.ForbiddenAccessException;
import com.rehund.healthcare.common.exception.ResourceNotFoundException;
import com.rehund.healthcare.common.exception.appointment.AppointmentConflictException;
import com.rehund.healthcare.common.exception.user.UserNotFoundException;
import com.rehund.healthcare.entity.appointment.Appointment;
import com.rehund.healthcare.entity.hospitaldoctor.Doctor;
import com.rehund.healthcare.entity.hospitaldoctor.Hospital;
import com.rehund.healthcare.entity.hospitaldoctor.HospitalDoctorFee;
import com.rehund.healthcare.entity.user.User;
import com.rehund.healthcare.model.appointment.AppointmentBookRequest;
import com.rehund.healthcare.model.appointment.AppointmentMeetingResponse;
import com.rehund.healthcare.model.appointment.AppointmentRescheduleRequest;
import com.rehund.healthcare.model.appointment.AppointmentResponse;
import com.rehund.healthcare.model.hospitaldoctor.DoctorResponse;
import com.rehund.healthcare.model.payment.PaymentResponse;
import com.rehund.healthcare.model.user.UserResponse;
import com.rehund.healthcare.repository.appointment.AppointmentRepository;
import com.rehund.healthcare.repository.hospitaldoctor.DoctorAvailabilityRepository;
import com.rehund.healthcare.repository.hospitaldoctor.DoctorRepository;
import com.rehund.healthcare.repository.hospitaldoctor.HospitalDoctorFeeRepository;
import com.rehund.healthcare.repository.hospitaldoctor.HospitalRepository;
import com.rehund.healthcare.repository.user.UserRepository;
import com.rehund.healthcare.service.hospitaldoctor.DoctorService;
import com.rehund.healthcare.service.payment.PaymentService;
import com.rehund.healthcare.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalDoctorFeeRepository hospitalDoctorFeeRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final HospitalRepository hospitalRepository;

    private final UserService userService;
    private final DoctorService doctorService;
    private final PaymentService paymentService;

    @Override
    @Transactional
    public AppointmentResponse bookAppointment(Long userId, AppointmentBookRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + request.getDoctorId()));

        Hospital hospital = hospitalRepository.findById(doctor.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + doctor.getHospitalId()));

        HospitalDoctorFee fee = hospitalDoctorFeeRepository.findByHospitalIdAndDoctorSpecializationIdAndConsultationType(
                doctor.getHospitalId(), request.getDoctorSpecializationId(), request.getConsultationType()
        ).orElseThrow(() -> new ResourceNotFoundException("Fee not found for doctor specialization id: " + request.getDoctorSpecializationId() + " and consultation type: " + request.getConsultationType()));

        // cek availability doctor
        boolean isDoctorAvailable = doctorAvailabilityRepository.isDoctorAvailable(
                doctor.getDoctorId(),
                request.getAppointmentDate(),
                request.getStartTime(),
                request.getEndTime(),
                fee.getConsultationType()
        );

        if (!isDoctorAvailable) {
            throw new AppointmentConflictException("Doctor is not available at the requested time");
        }

        List<Appointment> overlappingAppointments = appointmentRepository.findOverlappingAppointments(
                doctor.getDoctorId(),
                request.getAppointmentDate(),
                request.getStartTime(),
                request.getEndTime(),
                fee.getConsultationType()
        );

        if(!overlappingAppointments.isEmpty()){
            throw new AppointmentConflictException("There is already an appointment scheduled at the requested time");
        }

        Appointment bookedAppointment = Appointment
                .builder()
                .patientId(userId)
                .doctorId(request.getDoctorId())
                .hospitalId(doctor.getHospitalId())
                .doctorSpecializationId(request.getDoctorSpecializationId())
                .appointmentDate(request.getAppointmentDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .consultationType(fee.getConsultationType())
                .status(AppointmentStatus.PENDING)
                .build();

        appointmentRepository.save(bookedAppointment);

        PaymentResponse paymentResponse = paymentService.createPayment(bookedAppointment);

        return AppointmentResponse
                .builder()
                .appointmentId(bookedAppointment.getAppointmentId())
                .patientId(user.getUserId())
                .patientName(user.getUsername())
                .doctorId(doctor.getDoctorId())
                .doctorName(doctor.getName())
                .hospitalId(hospital.getHospitalId())
                .hospitalName(hospital.getName())
                .doctorSpecializationId(bookedAppointment.getDoctorSpecializationId())
                .consultationType(bookedAppointment.getConsultationType())
                .appointmentDate(bookedAppointment.getAppointmentDate())
                .startTime(bookedAppointment.getStartTime())
                .endTime(bookedAppointment.getEndTime())
                .status(bookedAppointment.getStatus())
                .paymentDetail(paymentResponse)
                .build();
    }

    @Override
    @Transactional
    public AppointmentResponse rescheduleAppointment(
            Long userId,
            Long appointmentId,
            AppointmentRescheduleRequest request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        Appointment appointment = appointmentRepository.findByIdAndLock(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        Doctor doctor = doctorRepository.findById(appointment.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found for appointment's doctor id: " + appointment.getDoctorId()));
        Hospital hospital = hospitalRepository.findById(appointment.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + doctor.getHospitalId()));

        if (!appointment.getPatientId().equals(userId)){
            throw new ForbiddenAccessException("Can't reschedule other's appointment");
        }

        if(
                appointment.getStatus() != AppointmentStatus.PENDING
                && appointment.getStatus() != AppointmentStatus.SCHEDULED
        ){
            throw new IllegalArgumentException("Only PENDING or SCHEDULED appointments can be rescheduled");
        }

        if(appointment.getAppointmentDate().isBefore(LocalDate.now())){
            throw new IllegalArgumentException("Cannot reschedule past appointments");
        }

        // cek availability doctor
        boolean isDoctorAvailable = doctorAvailabilityRepository.isDoctorAvailable(
                appointment.getDoctorId(),
                request.getAppointmentDate(),
                request.getStartTime(),
                request.getEndTime(),
                appointment.getConsultationType()
        );

        if (!isDoctorAvailable) {
            throw new AppointmentConflictException("Doctor is not available at the requested time");
        }

        List<Appointment> overlappingAppointments = appointmentRepository.findOverlappingAppointments(
                appointment.getDoctorId(),
                request.getAppointmentDate(),
                request.getStartTime(),
                request.getEndTime(),
                appointment.getConsultationType()
        );

        if(!overlappingAppointments.isEmpty()){
            throw new AppointmentConflictException("There is already an appointment scheduled at the requested time");
        }

        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getEndTime());

        appointmentRepository.save(appointment);

        paymentService.recalculatePayment(appointment);

        return mapAppointmentToAppointmentResponse(appointment, user, doctor, hospital);
    }

    @Override
    public AppointmentResponse findById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .map(this::mapAppointmentToAppointmentResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));
    }

    @Override
    public List<AppointmentResponse> listUserAppointments(Long userId) {
        List<Appointment> appointmentList = appointmentRepository.findByPatientIdOrderByAppointmentDateDescStartTimeDesc(userId);

        // untuk mengatasi N + 1 query problem pada payment detail
        // appointments => list of appointment id
        // List<Long> appointmentIds = appointmentList.stream()
        //         .map(Appointment::getAppointmentId)
        //         .toList();
        // paymentRepository.findByAppointmentIdIn(appointmentIds);
        // convertToAppointmentResponse with payment detail (List<Payment> payments, List<Appointment> appointments)

        return appointmentList.stream()
                .map(this::mapAppointmentToAppointmentResponse)
                .toList();
    }

    @Override
    public List<AppointmentResponse> listDoctorAppointments(Long doctorId) {
        List<Appointment> appointmentList = appointmentRepository.findByDoctorIdAndAppointmentDateOrderByStartTimeAsc(
                doctorId,
                LocalDate.now()
        );
        return appointmentList.stream()
                .map(this::mapAppointmentToAppointmentResponse)
                .toList();
    }

    @Override
    public AppointmentMeetingResponse getAppointmentMeetingDetails(Long userId, Long appointmentId) {
        UserResponse user = userService.getUserById(userId);
        AppointmentResponse appointmentResponse = findById(appointmentId);

        if(!appointmentResponse.getStatus().equals(AppointmentStatus.SCHEDULED)) {
            throw new IllegalArgumentException("Meeting details are only available for SCHEDULED appointments");
        }

        if(user.getRoles().contains(RoleType.DOCTOR)){
            DoctorResponse doctorResponse = doctorService.getByUserId(user.getUserId());

            if(!Objects.equals(doctorResponse.getDoctorId(), appointmentResponse.getDoctorId())){
                throw new ForbiddenAccessException("Can't access other's appointment meeting details");
            }

            return AppointmentMeetingResponse.builder()
                    .doctorId(doctorResponse.getDoctorId())
                    .appointmentStatus(appointmentResponse.getStatus())
                    .build();
        }

        if (!Objects.equals(user.getUserId(), appointmentResponse.getPatientId())) {
            throw new ForbiddenAccessException("Can't access other's appointment meeting details");
        }

        LocalDate today = LocalDate.now();
        if(!today.equals(appointmentResponse.getAppointmentDate())) {
            throw new IllegalArgumentException("Meeting details are only available on the appointment date");
        }

        LocalTime now = LocalTime.now();
        if(now.isBefore(appointmentResponse.getStartTime()) || now.isAfter(appointmentResponse.getEndTime())) {
            throw new IllegalArgumentException("Meeting details are only available during the appointment time");
        }

        return AppointmentMeetingResponse
                .builder()
                .patientId(appointmentResponse.getPatientId())
                .doctorId(appointmentResponse.getDoctorId())
                .appointmentStatus(appointmentResponse.getStatus())
                .build();
    }

    @Override
    @Transactional
    public void cancelAppointment(Long userId, Long appointmentId) {
        Appointment appointment = appointmentRepository.findByIdAndLock(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        if (!appointment.getPatientId().equals(userId)){
            throw new ForbiddenAccessException("Can't cancel other's appointment");
        }

        if(!appointment.getStatus().equals(AppointmentStatus.PENDING)){
            throw new IllegalArgumentException("Only PENDING appointments can be cancelled");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        paymentService.cancelPaymentForAppointment(appointmentId);
    }

    private AppointmentResponse mapAppointmentToAppointmentResponse(Appointment appointment, User user, Doctor doctor, Hospital hospital) {
        return AppointmentResponse
                .builder()
                .appointmentId(appointment.getAppointmentId())
                .patientId(appointment.getPatientId())
                .patientName(user.getUsername())
                .doctorId(appointment.getDoctorId())
                .doctorName(doctor.getName())
                .hospitalId(appointment.getHospitalId())
                .hospitalName(hospital.getName())
                .doctorSpecializationId(appointment.getDoctorSpecializationId())
                .consultationType(appointment.getConsultationType())
                .appointmentDate(appointment.getAppointmentDate())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus())
                .meetingId(appointment.getMeetingId())
                .build();
    }

    private AppointmentResponse mapAppointmentToAppointmentResponse(Appointment appointment) {

        PaymentResponse paymentResponse = paymentService.findByAppointmentId(appointment.getAppointmentId());
        return AppointmentResponse
                .builder()
                .appointmentId(appointment.getAppointmentId())
                .patientId(appointment.getPatientId())
                .doctorId(appointment.getDoctorId())
                .hospitalId(appointment.getHospitalId())
                .doctorSpecializationId(appointment.getDoctorSpecializationId())
                .consultationType(appointment.getConsultationType())
                .appointmentDate(appointment.getAppointmentDate())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus())
                .paymentDetail(paymentResponse)
                .meetingId(appointment.getMeetingId())
                .build();
    }
}
