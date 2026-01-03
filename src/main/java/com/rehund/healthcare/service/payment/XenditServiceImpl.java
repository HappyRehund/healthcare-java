package com.rehund.healthcare.service.payment;

import com.rehund.healthcare.common.constant.AppointmentStatus;
import com.rehund.healthcare.common.constant.PaymentStatus;
import com.rehund.healthcare.common.exception.ResourceNotFoundException;
import com.rehund.healthcare.common.exception.payment.PaymentException;
import com.rehund.healthcare.common.exception.user.UserNotFoundException;
import com.rehund.healthcare.entity.appointment.Appointment;
import com.rehund.healthcare.entity.payment.Payment;
import com.rehund.healthcare.entity.user.User;
import com.rehund.healthcare.model.payment.PaymentNotification;
import com.rehund.healthcare.model.payment.PaymentResponse;
import com.rehund.healthcare.repository.appointment.AppointmentRepository;
import com.rehund.healthcare.repository.payment.PaymentRepository;
import com.rehund.healthcare.repository.user.UserRepository;
import com.xendit.exception.XenditException;
import com.xendit.model.Invoice;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class XenditServiceImpl implements XenditService {

    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PaymentResponse createPayment(Payment payment) {

        Appointment appointment = appointmentRepository.findById(payment.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        User user = userRepository.findById(appointment.getPatientId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Map<String, Object> params = new HashMap<>();

        params.put("external_id", payment.getTransactionId());
        params.put("amount",payment.getAmount().doubleValue());
        params.put("payer_email",user.getEmail());
        params.put("description","Payment for Order: #" + payment.getTransactionId());

        Invoice invoice = null;

        try {
            invoice = Invoice.create(params);
        } catch (XenditException exception) {
            log.error("Error creating Xendit invoice: {}", exception.getMessage());
            throw new PaymentException(exception.getMessage());
        }

        payment.setXenditInvoiceId(invoice.getId());
        payment.setXenditPaymentStatus(invoice.getStatus());

        paymentRepository.save(payment);

        PaymentResponse paymentResponse = PaymentResponse.fromPayment(payment);
        paymentResponse.setPaymentUrl(invoice.getInvoiceUrl());

        return paymentResponse;
    }



    @Override
    @Transactional
    public void handlePaymentNotification(PaymentNotification payload) {
        String invoiceId = payload.getId();
        String status = payload.getStatus();

        Payment payment = paymentRepository.findByXenditInvoiceId(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with Xendit Invoice ID: " + invoiceId));

        // case PAID, EXPIRED, FAILED, PENDING.
        payment.setXenditPaymentStatus(status);

        switch (status){
            case "PAID" -> handleOnSuccess(payment);
            case "EXPIRED" -> handleOnCancellation(payment);
            case "FAILED" -> payment.setPaymentStatus(PaymentStatus.FAILED);
            case "PENDING" -> payment.setPaymentStatus(PaymentStatus.PENDING);
            default -> throw new IllegalArgumentException("Unknown payment status: " + status);
        }

        if(payload.getPaymentMethod() != null){
            payment.setPaymentMethod(payload.getPaymentMethod());
        }

        paymentRepository.save(payment);
    }

    private void handleOnSuccess(Payment payment){
        payment.setPaymentStatus(PaymentStatus.COMPLETED);

        Appointment appointment = appointmentRepository.findByIdAndLock(payment.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + payment.getAppointmentId()));

        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepository.save(appointment);
    }

    private void handleOnCancellation(Payment payment){
        payment.setPaymentStatus(PaymentStatus.CANCELED);

        Appointment appointment = appointmentRepository.findByIdAndLock(payment.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + payment.getAppointmentId()));

        appointment.setStatus(AppointmentStatus.CANCELLED);

        try {
            Invoice.expire(payment.getXenditInvoiceId());
        } catch (XenditException ex){
            log.error("Error expiring Xendit invoice: {}", ex.getMessage());
            throw new PaymentException(ex.getMessage());
        }
        appointmentRepository.save(appointment);
    }
}
