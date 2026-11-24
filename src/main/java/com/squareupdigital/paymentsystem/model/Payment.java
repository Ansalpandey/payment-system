package com.squareupdigital.paymentsystem.model;

import com.squareupdigital.paymentsystem.enums.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(
    name = "payments",
    indexes = {
      @Index(name = "idx_payment_sender", columnList = "sender_wallet_id"),
      @Index(name = "idx_payment_receiver", columnList = "receiver_wallet_id")
    })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "sender_wallet_id", nullable = false)
  private UUID senderWalletId;

  @Column(name = "receiver_wallet_id", nullable = false)
  private UUID receiverWalletId;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false, length = 3)
  private String currency;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentStatus status;

  @Column(name = "reference_id", unique = true)
  private String referenceId;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Instant createdAt;
}
