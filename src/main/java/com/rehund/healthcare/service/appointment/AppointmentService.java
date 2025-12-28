package com.rehund.healthcare.service.appointment;

import com.rehund.healthcare.model.appointment.AppointmentBookRequest;
import com.rehund.healthcare.model.appointment.AppointmentRescheduleRequest;
import com.rehund.healthcare.model.appointment.AppointmentResponse;

import java.util.List;

public interface AppointmentService {
    AppointmentResponse bookAppointment(AppointmentBookRequest request);

    AppointmentResponse rescheduleAppointment(Long userId, Long appointmentId, AppointmentRescheduleRequest request);

    AppointmentResponse findById(Long appointmentId);

    List<AppointmentResponse> listUserAppointments(Long userId);

    List<AppointmentResponse> listDoctorAppointments(Long doctorId);

    void cancelAppointment(Long userId, Long appointmentId);
}
