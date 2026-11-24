package com.squareupdigital.paymentsystem.model;

import com.squareupdigital.paymentsystem.enums.WalletStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Entity
@Table(
    name = "wallets",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_wallet_user_currency",
          columnNames = {"user_id", "currency"})
    })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Column(nullable = false, length = 3)
  private String currency;

  @Column(name = "balance", nullable = false, precision = 19, scale = 2)
  private BigDecimal balance;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private WalletStatus status;

  @Version private Long version;
}
