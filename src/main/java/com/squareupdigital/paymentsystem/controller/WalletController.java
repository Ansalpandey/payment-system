package com.squareupdigital.paymentsystem.controller;

import com.squareupdigital.paymentsystem.dto.AddAmountRequest;
import com.squareupdigital.paymentsystem.repository.WalletRepository;
import com.squareupdigital.paymentsystem.service.WalletService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
    private WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public String addAmount(@RequestBody AddAmountRequest amountRequest) {
        return walletService.addAmount(amountRequest);
    }

}
