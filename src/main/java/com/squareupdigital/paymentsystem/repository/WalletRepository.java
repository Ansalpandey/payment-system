package com.squareupdigital.paymentsystem.repository;

import com.squareupdigital.paymentsystem.model.Wallet;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

  Optional<Wallet> findByUserId(UUID userId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
          SELECT w FROM Wallet w
          WHERE w.user.id = :userId
      """)
  Optional<Wallet> findByUserIdForUpdate(@Param("userId") UUID userId);
}
