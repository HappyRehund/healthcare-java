package com.rehund.healthcare.service.payment;

import com.rehund.healthcare.entity.payment.Payment;
import com.rehund.healthcare.model.payment.PaymentNotification;
import com.rehund.healthcare.model.payment.PaymentResponse;

public interface XenditService {

    PaymentResponse createPayment(Payment payment);

    void handlePaymentNotification(PaymentNotification payload);
}
