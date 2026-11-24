package com.squareupdigital.paymentsystem.model;

import com.squareupdigital.paymentsystem.enums.EntryType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(
    name = "ledger_entries",
    indexes = {
      @Index(name = "idx_ledger_wallet", columnList = "wallet_id"),
      @Index(name = "idx_ledger_payment", columnList = "payment_id")
    })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedgerEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "payment_id", nullable = false)
  private UUID paymentId;

  @Column(name = "wallet_id", nullable = false)
  private UUID walletId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EntryType entryType;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false, length = 3)
  private String currency;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Instant createdAt;
}
