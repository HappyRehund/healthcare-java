package com.rehund.healthcare.controller.webhook;

import com.rehund.healthcare.model.payment.PaymentNotification;
import com.rehund.healthcare.service.payment.XenditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook/xendit")
@RequiredArgsConstructor
public class XenditController {

    private final XenditService xenditService;

    @PostMapping
    public ResponseEntity<String> handleXenditNotification(
            @RequestBody PaymentNotification payload
            )
    {
        try {
            xenditService.handlePaymentNotification(payload);
            return ResponseEntity.ok("Notification processed");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing notification: " + e.getMessage());
        }

    }
}
