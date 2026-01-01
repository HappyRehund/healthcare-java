package com.rehund.healthcare.model.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.rehund.healthcare.common.constant.PaymentStatus;
import com.rehund.healthcare.entity.payment.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {
    private Long paymentId;
    private Long appointmentId;
    private BigDecimal amount;
    private String paymentMethod;
    private String transactionId;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;

    private String externalId;
    private String externalStatus;
    private String paymentUrl;

    public static PaymentResponse fromPayment(Payment payment){
        return PaymentResponse
                .builder()
                .paymentId(payment.getPaymentId())
                .appointmentId(payment.getAppointmentId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .paymentStatus(payment.getPaymentStatus())
                .externalId(payment.getXenditInvoiceId())
                .externalStatus(payment.getXenditPaymentStatus())
                .createdAt(payment.getCreatedAt())
                .build();

    }
}
