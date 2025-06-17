package com.example.paymentsservice;

import com.example.paymentsservice.model.Account;
import com.example.paymentsservice.model.Payment;
import com.example.paymentsservice.service.PaymentService;
import com.example.paymentsservice.repository.AccountRepository;
import com.example.paymentsservice.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void createAccount_ShouldCreateNewAccount() {
        // Arrange
        Long userId = 1L;
        Account account = new Account();
        account.setUserId(userId);
        account.setBalance(BigDecimal.ZERO);
        
        when(accountRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // Act
        Account result = paymentService.createAccount(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(BigDecimal.ZERO, result.getBalance());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void depositMoney_ShouldIncreaseBalance() {
        // Arrange
        Long userId = 1L;
        BigDecimal amount = new BigDecimal("100.00");
        Account account = new Account();
        account.setUserId(userId);
        account.setBalance(BigDecimal.ZERO);
        
        when(accountRepository.findByUserId(userId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // Act
        Account result = paymentService.depositMoney(userId, amount);

        // Assert
        assertNotNull(result);
        assertEquals(amount, result.getBalance());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void processPayment_ShouldCreatePaymentAndUpdateBalance() {
        // Arrange
        Long userId = 1L;
        BigDecimal amount = new BigDecimal("50.00");
        Account account = new Account();
        account.setUserId(userId);
        account.setBalance(new BigDecimal("100.00"));
        
        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setAmount(amount);
        
        when(accountRepository.findByUserId(userId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Act
        boolean result = paymentService.processPayment(userId, amount);

        // Assert
        assertTrue(result);
        verify(accountRepository).save(any(Account.class));
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void getBalance_ShouldReturnCurrentBalance() {
        // Arrange
        Long userId = 1L;
        BigDecimal balance = new BigDecimal("100.00");
        Account account = new Account();
        account.setUserId(userId);
        account.setBalance(balance);
        
        when(accountRepository.findByUserId(userId)).thenReturn(Optional.of(account));

        // Act
        BigDecimal result = paymentService.getBalance(userId);

        // Assert
        assertEquals(balance, result);
    }
} 