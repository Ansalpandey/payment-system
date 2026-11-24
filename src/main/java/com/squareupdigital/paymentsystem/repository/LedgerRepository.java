package com.squareupdigital.paymentsystem.repository;

import com.squareupdigital.paymentsystem.model.LedgerEntry;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerRepository extends JpaRepository<LedgerEntry, UUID> {


  @Query("""
        SELECT l
        FROM LedgerEntry l
        WHERE l.walletId = :walletId
        AND (
            l.createdAt < :createdAt
            OR (
                l.createdAt = :createdAt
                AND l.id < :id
            )
        )
        ORDER BY l.createdAt DESC, l.id DESC
    """)
  List<LedgerEntry> findNextPage(
          @Param("walletId") UUID walletId,
          @Param("createdAt") Instant createdAt,
          @Param("id") UUID id,
          Pageable pageable
  );

  List<LedgerEntry> findByWalletIdOrderByCreatedAtDescIdDesc(
          UUID walletId,
          Pageable pageable
  );
}
