package com.squareupdigital.paymentsystem.repository;

import com.squareupdigital.paymentsystem.model.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LedgerRepository extends JpaRepository<LedgerEntry, UUID> {
    List<LedgerEntry> findByPaymentId(UUID paymentId);

    List<LedgerEntry> findByWalletIdOrderByCreatedAtDesc(UUID walletId);
}
