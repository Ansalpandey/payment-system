package com.squareupdigital.paymentsystem.controller;

import com.squareupdigital.paymentsystem.dto.CursorPageResponse;
import com.squareupdigital.paymentsystem.dto.LedgerEntryResponse;
import com.squareupdigital.paymentsystem.service.LedgerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ledger")
public class LedgerController {
  private LedgerService ledgerService;

  public LedgerController(LedgerService ledgerService) {
    this.ledgerService = ledgerService;
  }

  @GetMapping
  public ResponseEntity<CursorPageResponse<LedgerEntryResponse>> getLedger(
      @RequestParam(required = false) String cursor, @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(ledgerService.getLedgerOfUser(cursor, size));
  }
}
