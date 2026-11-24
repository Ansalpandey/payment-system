package com.squareupdigital.paymentsystem.dto;

import com.squareupdigital.paymentsystem.enums.EntryType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LedgerEntryResponse(
        UUID id,
        UUID paymentId,
        EntryType type,
        BigDecimal amount,
        String currency,
        Instant createdAt
) {}
