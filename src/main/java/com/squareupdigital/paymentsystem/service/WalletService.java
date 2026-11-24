package com.squareupdigital.paymentsystem.service;

import com.squareupdigital.paymentsystem.dto.AddAmountRequest;
import com.squareupdigital.paymentsystem.enums.EntryType;
import com.squareupdigital.paymentsystem.enums.WalletStatus;
import com.squareupdigital.paymentsystem.helper.AuthenticationHelper;
import com.squareupdigital.paymentsystem.model.LedgerEntry;
import com.squareupdigital.paymentsystem.model.Wallet;
import com.squareupdigital.paymentsystem.repository.LedgerRepository;
import com.squareupdigital.paymentsystem.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {
  private WalletRepository walletRepository;
  private LedgerRepository ledgerRepository;
  private AuthenticationHelper helper;

  public WalletService(WalletRepository walletRepository, LedgerRepository ledgerRepository, AuthenticationHelper helper) {
    this.walletRepository = walletRepository;
    this.ledgerRepository = ledgerRepository;
    this.helper=helper;
  }

  @Transactional
  public String addAmount(AddAmountRequest amountRequest) {

    if (amountRequest == null || amountRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount must be greater than zero");
    }
    UUID userId = helper.getAuthenticatedUserId();

    Wallet wallet =
        walletRepository
            .findByUserId(userId)
            .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

    if (wallet.getStatus() != WalletStatus.ACTIVE) {
      throw new IllegalStateException("Wallet is not active");
    }

    wallet.setBalance(wallet.getBalance().add(amountRequest.getAmount()));

    LedgerEntry entry = new LedgerEntry();

    entry.setAmount(amountRequest.getAmount());
    entry.setEntryType(EntryType.CREDIT);
    entry.setCurrency("INR");
    entry.setWalletId(wallet.getId());
    entry.setPaymentId(UUID.randomUUID());
    walletRepository.save(wallet);
    ledgerRepository.save(entry);

    return "Amount added successfully";
  }
}
