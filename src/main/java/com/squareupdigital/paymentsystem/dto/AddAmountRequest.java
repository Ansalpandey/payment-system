package com.squareupdigital.paymentsystem.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddAmountRequest {
    private BigDecimal amount;
}
