package com.squareupdigital.paymentsystem.service;

import com.squareupdigital.paymentsystem.dto.SendPaymentRequest;
import com.squareupdigital.paymentsystem.enums.EntryType;
import com.squareupdigital.paymentsystem.enums.PaymentStatus;
import com.squareupdigital.paymentsystem.enums.WalletStatus;
import com.squareupdigital.paymentsystem.helper.AuthenticationHelper;
import com.squareupdigital.paymentsystem.model.LedgerEntry;
import com.squareupdigital.paymentsystem.model.Payment;
import com.squareupdigital.paymentsystem.model.Wallet;
import com.squareupdigital.paymentsystem.repository.LedgerRepository;
import com.squareupdigital.paymentsystem.repository.PaymentRepository;
import com.squareupdigital.paymentsystem.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
  private PaymentRepository paymentRepository;
  private WalletRepository walletRepository;
  private AuthenticationHelper helper;
  private LedgerRepository ledgerRepository;

  public PaymentService(
      PaymentRepository paymentRepository,
      WalletRepository walletRepository,
      LedgerRepository ledgerRepository,
      AuthenticationHelper helper) {
    this.paymentRepository = paymentRepository;
    this.walletRepository = walletRepository;
    this.helper = helper;
    this.ledgerRepository = ledgerRepository;
  }

  @Transactional
  public String transferAmount(SendPaymentRequest request) {

    BigDecimal amount = request.getAmount();

    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Invalid transfer amount");
    }

    UUID senderUserId = helper.getAuthenticatedUserId();
    UUID receiverUserId = request.getReceiverId();

    if (receiverUserId == null) {
      throw new IllegalArgumentException("Receiver user ID is required");
    }

    if (senderUserId.equals(receiverUserId)) {
      throw new IllegalArgumentException("Cannot transfer money to yourself");
    }

    Wallet sender =
        walletRepository
            .findByUserId(senderUserId)
            .orElseThrow(() -> new EntityNotFoundException("Sender wallet not found"));

    Wallet receiver =
        walletRepository
            .findByUserId(receiverUserId)
            .orElseThrow(() -> new EntityNotFoundException("Receiver wallet not found"));

    boolean senderFirst = sender.getId().compareTo(receiver.getId()) < 0;

    UUID firstUserId = senderFirst ? senderUserId : receiverUserId;
    UUID secondUserId = senderFirst ? receiverUserId : senderUserId;

    Wallet first =
        walletRepository
            .findByUserIdForUpdate(firstUserId)
            .orElseThrow(() -> new EntityNotFoundException("First wallet not found"));

    Wallet second =
        walletRepository
            .findByUserIdForUpdate(secondUserId)
            .orElseThrow(() -> new EntityNotFoundException("Second wallet not found"));

    Wallet lockedSender = senderFirst ? first : second;
    Wallet lockedReceiver = senderFirst ? second : first;

    if (lockedSender.getStatus() != WalletStatus.ACTIVE
        || lockedReceiver.getStatus() != WalletStatus.ACTIVE) {
      throw new IllegalStateException("Wallet is not active");
    }

    if (!lockedSender.getCurrency().equals(lockedReceiver.getCurrency())) {
      throw new IllegalArgumentException("Currency mismatch");
    }

    if (lockedSender.getBalance().compareTo(amount) < 0) {
      throw new IllegalStateException("Insufficient balance");
    }

    lockedSender.setBalance(lockedSender.getBalance().subtract(amount));

    lockedReceiver.setBalance(lockedReceiver.getBalance().add(amount));

    Payment payment = new Payment();
    payment.setAmount(amount);
    payment.setSenderWalletId(lockedSender.getId());
    payment.setReceiverWalletId(lockedReceiver.getId());
    payment.setCurrency(lockedSender.getCurrency());
    payment.setStatus(PaymentStatus.SUCCESS);

    paymentRepository.save(payment);

    LedgerEntry debitEntry =
        LedgerEntry.builder()
            .paymentId(payment.getId())
            .walletId(lockedSender.getId())
            .entryType(EntryType.DEBIT)
            .amount(amount)
            .currency(lockedSender.getCurrency())
            .build();
    LedgerEntry creditEntry =
        LedgerEntry.builder()
            .paymentId(payment.getId())
            .walletId(lockedReceiver.getId())
            .entryType(EntryType.CREDIT)
            .amount(amount)
            .currency(lockedReceiver.getCurrency())
            .build();
    ledgerRepository.saveAll(List.of(debitEntry, creditEntry));

    return "Transfer completed successfully";
  }
}
