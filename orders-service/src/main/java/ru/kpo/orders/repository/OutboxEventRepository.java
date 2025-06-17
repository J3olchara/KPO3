package ru.kpo.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kpo.orders.model.OutboxEvent;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByProcessedFalseOrderByCreatedAtAsc();
} 