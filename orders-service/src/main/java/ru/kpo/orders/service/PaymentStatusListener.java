package ru.kpo.orders.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.kpo.orders.model.Order;

@Service
public class PaymentStatusListener {
    private static final Logger log = LoggerFactory.getLogger(PaymentStatusListener.class);
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "payment-status", groupId = "orders-group")
    public void handlePaymentStatus(String message) {
        try {
            JsonNode paymentStatus = objectMapper.readTree(message);
            String orderId = paymentStatus.get("orderId").asText();
            String status = paymentStatus.get("status").asText();

            Order.OrderStatus orderStatus = "SUCCESS".equals(status) 
                ? Order.OrderStatus.FINISHED 
                : Order.OrderStatus.CANCELLED;

            orderService.updateOrderStatus(Long.parseLong(orderId), orderStatus);
            log.info("Updated order status: {} -> {}", orderId, orderStatus);
        } catch (Exception e) {
            log.error("Error processing payment status", e);
        }
    }
} 