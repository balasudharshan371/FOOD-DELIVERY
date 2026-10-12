package com.foodiehub.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
public class PaymentService {

    public record PaymentResult(boolean success, String transactionId, String message) {}

    public PaymentResult processPayment(String paymentMethod, BigDecimal amount, String orderNumber) {
        if ("CASH_ON_DELIVERY".equalsIgnoreCase(paymentMethod)) {
            return new PaymentResult(true, "COD-" + UUID.randomUUID().toString().substring(0, 8), "Cash on delivery selected");
        }

        // Simulated online payment for intermediate/development level
        log.info("Processing simulated payment of ₹{} for order {}", amount, orderNumber);
        String txnId = "SIM-TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();

        return new PaymentResult(true, txnId, "Simulated payment successful (Demo Mode)");
    }
}
