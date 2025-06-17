package ru.kpo.payments.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kpo.payments.model.Account;
import ru.kpo.payments.model.PaymentTransaction;
import ru.kpo.payments.repository.AccountRepository;
import ru.kpo.payments.repository.PaymentTransactionRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private PaymentTransactionRepository transactionRepository;
    
    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Transactional
    public Account createAccount(String userId) {
        if (accountRepository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException("Account already exists for user: " + userId);
        }
        Account account = new Account(userId);
        return accountRepository.save(account);
    }

    @Transactional
    public Account addFunds(String userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Account not found for user: " + userId));

        account.setBalance(account.getBalance().add(amount));
        return accountRepository.save(account);
    }

    public Optional<Account> getAccount(String userId) {
        return accountRepository.findByUserId(userId);
    }

    @Transactional
    public PaymentTransaction processPayment(String userId, String orderId, BigDecimal amount) {
        Account account = accountRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new IllegalStateException("Account not found for user: " + userId));

        PaymentTransaction transaction = new PaymentTransaction(userId, orderId, amount);

        if (account.getBalance().compareTo(amount) >= 0) {
            account.setBalance(account.getBalance().subtract(amount));
            transaction.setStatus(PaymentTransaction.PaymentStatus.SUCCESS);
        } else {
            transaction.setStatus(PaymentTransaction.PaymentStatus.FAILED);
        }

        accountRepository.save(account);
        transaction = transactionRepository.save(transaction);

        // Сохраняем событие в outbox для последующей отправки
        kafkaProducerService.sendPaymentStatus(transaction);

        return transaction;
    }
} 