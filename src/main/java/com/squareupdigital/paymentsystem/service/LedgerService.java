package com.squareupdigital.paymentsystem.service;

import com.squareupdigital.paymentsystem.dto.CursorPageResponse;
import com.squareupdigital.paymentsystem.dto.LedgerEntryResponse;
import com.squareupdigital.paymentsystem.helper.AuthenticationHelper;
import com.squareupdigital.paymentsystem.model.LedgerEntry;
import com.squareupdigital.paymentsystem.model.Wallet;
import com.squareupdigital.paymentsystem.repository.LedgerRepository;
import com.squareupdigital.paymentsystem.repository.WalletRepository;
import com.squareupdigital.paymentsystem.utils.LedgerCursorUtil;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LedgerService {
  private LedgerRepository ledgerRepository;
  private WalletRepository walletRepository;
  private AuthenticationHelper helper;
  private LedgerCursorUtil util;

  public LedgerService(
      LedgerRepository ledgerRepository,
      AuthenticationHelper helper,
      WalletRepository walletRepository,
      LedgerCursorUtil util) {
    this.ledgerRepository = ledgerRepository;
    this.helper = helper;
    this.walletRepository = walletRepository;
    this.util = util;
  }

  @Transactional(readOnly = true)
  public CursorPageResponse<LedgerEntryResponse> getLedgerOfUser(String cursor, int pageSize) {

    if (pageSize < 1 || pageSize > 100) {
      throw new IllegalArgumentException("Page size must be between 1 and 100");
    }

    UUID userId = helper.getAuthenticatedUserId();

    Wallet wallet =
        walletRepository
            .findByUserId(userId)
            .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

    Pageable pageable = PageRequest.of(0, pageSize + 1);

    List<LedgerEntry> entries;

    if (cursor == null || cursor.isBlank()) {
      entries = ledgerRepository.findByWalletIdOrderByCreatedAtDescIdDesc(wallet.getId(), pageable);
    } else {
      LedgerCursorUtil.Cursor decoded = util.decode(cursor);

      entries =
          ledgerRepository.findNextPage(
              wallet.getId(), decoded.createdAt(), decoded.id(), pageable);
    }

    boolean hasNext = entries.size() > pageSize;

    if (hasNext) {
      entries = entries.subList(0, pageSize);
    }

    String nextCursor = null;

    if (hasNext && !entries.isEmpty()) {
      LedgerEntry lastEntry = entries.get(entries.size() - 1);
      nextCursor = util.encode(lastEntry);
    }

    List<LedgerEntryResponse> content =
        entries.stream()
            .map(entry ->
                new LedgerEntryResponse(
                    entry.getId(),
                    entry.getPaymentId(),
                    entry.getEntryType(),
                    entry.getAmount(),
                    entry.getCurrency(),
                    entry.getCreatedAt()))
            .toList();

    return new CursorPageResponse<>(content, nextCursor, hasNext);
  }
}
