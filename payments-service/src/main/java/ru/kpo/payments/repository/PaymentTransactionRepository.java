package ru.kpo.payments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kpo.payments.model.PaymentTransaction;

import java.util.List;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    List<PaymentTransaction> findByProcessedFalseOrderByCreatedAtAsc();
} 