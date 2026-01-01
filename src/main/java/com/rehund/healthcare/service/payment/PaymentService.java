package com.rehund.healthcare.service.payment;

import com.rehund.healthcare.entity.appointment.Appointment;
import com.rehund.healthcare.model.payment.PaymentResponse;

public interface PaymentService {
    PaymentResponse createPayment(Appointment appointment);
    PaymentResponse findByAppointmentId(Long appointmentId);
    PaymentResponse cancelPayment(Long paymentId);
    PaymentResponse cancelPaymentForAppointment(Long appointmentId);
    PaymentResponse recalculatePayment(Appointment appointment);
}
