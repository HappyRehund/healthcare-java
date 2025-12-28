package com.rehund.healthcare.repository.payment;

import com.rehund.healthcare.common.constant.PaymentStatus;
import com.rehund.healthcare.entity.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query(
            value = """
            SELECT * FROM payments p
            WHERE p.payment_id = :paymentId
            FOR UPDATE
            """,
            nativeQuery = true
    )
    Optional<Payment> findByIdAndLock(Long paymentId);
    @Query(
            value = """
            SELECT * FROM payments
            WHERE appointment_id = :appointmentId
            FOR UPDATE
            """,
            nativeQuery = true
    )
    Optional<Payment> findByAppointmentIdAndLock(Long appointmentId);

    @Query(
            value = """
            SELECT * FROM payments p
            WHERE p.appointment_id = :appointmentId
            AND p.payment_status = 'COMPLETED'
            """,
            nativeQuery = true
    )
    Optional<Payment> findCompletedPaymentByAppointmentId(Long appointmentId);

    @Query(
            value = """
            SELECT * FROM payments p
            WHERE p.payment_reference = :paymentReference
            FOR UPDATE
            """,
            nativeQuery = true
    )
    List<Payment> findByPaymentStatus(PaymentStatus status);

    Optional<Payment> findByAppointmentId(Long appointmentId);
}
