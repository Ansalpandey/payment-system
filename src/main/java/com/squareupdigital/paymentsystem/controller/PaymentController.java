package com.squareupdigital.paymentsystem.controller;

import com.squareupdigital.paymentsystem.dto.SendPaymentRequest;
import com.squareupdigital.paymentsystem.service.PaymentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private PaymentService paymentService;

    public PaymentController(PaymentService service){
        this.paymentService=service;
    }

    @PostMapping
    public String transferAmount(@RequestBody SendPaymentRequest request){
        return paymentService.transferAmount(request);
    }
}
