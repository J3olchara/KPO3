package ru.kpo.orders.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpo.orders.model.OutboxEvent;
import ru.kpo.orders.repository.OutboxEventRepository;

import java.util.List;

@Service
public class OutboxEventProcessor {
    private static final Logger log = LoggerFactory.getLogger(OutboxEventProcessor.class);
    
    @Autowired
    private OutboxEventRepository outboxEventRepository;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    private static final String ORDER_PAYMENT_TOPIC = "order-payment";

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void processOutboxEvents() {
        List<OutboxEvent> events = outboxEventRepository.findByProcessedFalseOrderByCreatedAtAsc();
        
        for (OutboxEvent event : events) {
            try {
                kafkaTemplate.send(ORDER_PAYMENT_TOPIC, event.getAggregateId(), event.getPayload());
                event.setProcessed(true);
                outboxEventRepository.save(event);
                log.info("Processed outbox event: {}", event.getId());
            } catch (Exception e) {
                log.error("Error processing outbox event: {}", event.getId(), e);
            }
        }
    }
} 