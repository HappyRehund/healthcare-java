package com.rehund.healthcare.service.payment;

import com.rehund.healthcare.entity.appointment.Appointment;
import com.rehund.healthcare.model.payment.PaymentResponse;

public interface PaymentService {
    PaymentResponse createPayment(Appointment appointment);
    PaymentResponse findByAppointmentId(Long appointmentId);
}
