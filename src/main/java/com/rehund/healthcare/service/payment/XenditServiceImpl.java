package com.rehund.healthcare.service.payment;

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
    public void handlePaymentNotification(PaymentNotification payload) {

    }
}
