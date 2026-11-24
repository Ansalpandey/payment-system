package com.squareupdigital.paymentsystem.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class SendPaymentRequest {
    private UUID receiverId;
    private BigDecimal amount;
}
