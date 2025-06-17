package ru.kpo.payments.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.kpo.payments.model.PaymentTransaction;

@Service
public class KafkaProducerService {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private static final String PAYMENT_STATUS_TOPIC = "payment-status";

    public void sendPaymentStatus(PaymentTransaction transaction) {
        try {
            String message = objectMapper.writeValueAsString(transaction);
            kafkaTemplate.send(PAYMENT_STATUS_TOPIC, transaction.getOrderId(), message);
            log.info("Payment status sent to Kafka for order: {}", transaction.getOrderId());
        } catch (Exception e) {
            log.error("Error sending payment status to Kafka", e);
            throw new RuntimeException("Error sending payment status to Kafka", e);
        }
    }
} 