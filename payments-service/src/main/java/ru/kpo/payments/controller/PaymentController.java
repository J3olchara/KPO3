package ru.kpo.payments.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kpo.payments.model.Account;
import ru.kpo.payments.service.PaymentService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestParam String userId) {
        return ResponseEntity.ok(paymentService.createAccount(userId));
    }

    @PostMapping("/accounts/deposit")
    public ResponseEntity<Account> addFunds(
            @RequestParam String userId,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(paymentService.addFunds(userId, amount));
    }

    @GetMapping("/accounts/{userId}")
    public ResponseEntity<Account> getAccount(@PathVariable String userId) {
        return paymentService.getAccount(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 