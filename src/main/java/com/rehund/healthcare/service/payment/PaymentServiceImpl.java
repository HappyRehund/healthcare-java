package com.rehund.healthcare.service.payment;

import com.rehund.healthcare.common.constant.AppointmentStatus;
import com.rehund.healthcare.common.constant.PaymentStatus;
import com.rehund.healthcare.common.exception.ResourceNotFoundException;
import com.rehund.healthcare.entity.appointment.Appointment;
import com.rehund.healthcare.entity.hospitaldoctor.DoctorSpecialization;
import com.rehund.healthcare.entity.payment.Payment;
import com.rehund.healthcare.model.payment.PaymentResponse;
import com.rehund.healthcare.repository.hospitaldoctor.DoctorSpecializationRepository;
import com.rehund.healthcare.repository.payment.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final DoctorSpecializationRepository doctorSpecializationRepository;

    private final XenditService xenditService;

    @Override
    public PaymentResponse createPayment(Appointment appointment) {
        if (!appointment.getStatus().equals(AppointmentStatus.PENDING)){
            throw new IllegalArgumentException("Payment can only be created for appointments with PENDING status.");
        }

        DoctorSpecialization doctorSpecialization = doctorSpecializationRepository.findById(appointment.getDoctorSpecializationId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Doctor Specialization ID"));

        BigDecimal hourlyFee = doctorSpecialization.getBaseFee();

        BigDecimal amount = calculateAmount(appointment, hourlyFee);

        String transactionId = UUID.randomUUID().toString();

        Payment payment = Payment
                .builder()
                .appointmentId(appointment.getAppointmentId())
                .amount(amount)
                .paymentMethod("NOT_SELECTED")
                .transactionId(transactionId)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);

        return xenditService.createPayment(payment);
    }

    @Override
    public PaymentResponse findByAppointmentId(Long appointmentId) {
        return paymentRepository.findByAppointmentId(appointmentId)
                .map(PaymentResponse::fromPayment)
                .orElse(null);
    }

    @Override
    @Transactional
    public PaymentResponse cancelPayment(Long paymentId) {

        Payment payment = paymentRepository.findByIdAndLock(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment cannot be canceled as it is not in PENDING status.");
        }

        payment.setPaymentStatus(PaymentStatus.CANCELED);
        paymentRepository.save(payment);

        return PaymentResponse.fromPayment(payment);
    }

    @Override
    @Transactional
    public PaymentResponse cancelPaymentForAppointment(Long appointmentId) {
        Payment payment = paymentRepository.findByAppointmentIdAndLock(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for Appointment ID: " + appointmentId));

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment cannot be canceled as it is not in PENDING status.");
        }

        payment.setPaymentStatus(PaymentStatus.CANCELED);
        paymentRepository.save(payment);

        return PaymentResponse.fromPayment(payment);
    }

    @Override
    @Transactional
    public PaymentResponse recalculatePayment(Appointment appointment) {
        Payment payment = paymentRepository.findByAppointmentIdAndLock(appointment.getAppointmentId())
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for Appointment ID: " + appointment.getAppointmentId()));

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment can only be recalculated if it is in PENDING status.");
        }

        DoctorSpecialization doctorSpecialization = doctorSpecializationRepository.findById(appointment.getDoctorSpecializationId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Doctor Specialization ID"));

        BigDecimal hourlyFee = doctorSpecialization.getBaseFee();
        BigDecimal newAmount = calculateAmount(appointment, hourlyFee);
        payment.setAmount(newAmount);
        paymentRepository.save(payment);

        return PaymentResponse.fromPayment(payment);
    }

    private BigDecimal calculateAmount(Appointment appointment, BigDecimal hourlyFee) {
        Duration duration = Duration.between(appointment.getStartTime(), appointment.getEndTime());

        long hours = duration.toHours();

        if(duration.toMinutesPart() > 0 || duration.toSecondsPart() > 0){
            hours +=1;
        }

        return hourlyFee.multiply(BigDecimal.valueOf(hours)).setScale(2, RoundingMode.HALF_UP);
    }
}
