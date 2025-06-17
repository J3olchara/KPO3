package ru.kpo.orders.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kpo.orders.model.Order;
import ru.kpo.orders.model.OutboxEvent;
import ru.kpo.orders.repository.OrderRepository;
import ru.kpo.orders.repository.OutboxEventRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OutboxEventRepository outboxEventRepository;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public Order createOrder(String userId, BigDecimal amount, String description) {
        Order order = new Order(userId, amount, description);
        order = orderRepository.save(order);

        try {
            String payload = objectMapper.writeValueAsString(order);
            OutboxEvent outboxEvent = new OutboxEvent(
                "Order",
                order.getId().toString(),
                "OrderCreated",
                payload
            );
            outboxEventRepository.save(outboxEvent);
            log.info("Created order with ID: {}", order.getId());
        } catch (Exception e) {
            log.error("Error creating outbox event", e);
            throw new RuntimeException("Error creating outbox event", e);
        }

        return order;
    }

    public List<Order> getOrders(String userId) {
        return orderRepository.findByUserId(userId);
    }

    public Optional<Order> getOrder(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Transactional
    public void updateOrderStatus(Long orderId, Order.OrderStatus status) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(status);
            orderRepository.save(order);
            log.info("Updated order status: {} -> {}", orderId, status);
        });
    }
} 