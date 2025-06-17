package ru.kpo.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kpo.orders.model.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(String userId);
} 