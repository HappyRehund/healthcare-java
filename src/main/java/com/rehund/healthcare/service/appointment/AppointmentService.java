package com.rehund.healthcare.service.appointment;

import com.rehund.healthcare.model.appointment.AppointmentRequest;
import com.rehund.healthcare.model.appointment.AppointmentResponse;

public interface AppointmentService {
    AppointmentResponse bookAppointment(AppointmentRequest request);
}
