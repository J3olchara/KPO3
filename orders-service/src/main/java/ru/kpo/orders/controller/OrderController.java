package ru.kpo.orders.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kpo.orders.model.Order;
import ru.kpo.orders.service.OrderService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestParam String userId,
            @RequestParam BigDecimal amount,
            @RequestParam String description) {
        return ResponseEntity.ok(orderService.createOrder(userId, amount, description));
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders(@RequestParam String userId) {
        return ResponseEntity.ok(orderService.getOrders(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
        return orderService.getOrder(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 