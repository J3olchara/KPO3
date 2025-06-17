package com.example.ordersservice;

import com.example.ordersservice.model.Order;
import com.example.ordersservice.service.OrderService;
import com.example.ordersservice.repository.OrderRepository;
import com.example.ordersservice.kafka.OrderProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderProducer orderProducer;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_ShouldSaveOrderAndSendMessage() {
        // Arrange
        Order order = new Order();
        order.setUserId(1L);
        order.setAmount(new BigDecimal("100.00"));
        
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        Order result = orderService.createOrder(order);

        // Assert
        assertNotNull(result);
        verify(orderRepository).save(any(Order.class));
        verify(orderProducer).sendOrder(any(Order.class));
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        Optional<Order> result = orderService.getOrderById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void getAllOrdersByUserId_ShouldReturnListOfOrders() {
        // Arrange
        Order order1 = new Order();
        order1.setUserId(1L);
        Order order2 = new Order();
        order2.setUserId(1L);
        
        List<Order> orders = Arrays.asList(order1, order2);
        when(orderRepository.findAllByUserId(1L)).thenReturn(orders);

        // Act
        List<Order> result = orderService.getAllOrdersByUserId(1L);

        // Assert
        assertEquals(2, result.size());
        verify(orderRepository).findAllByUserId(1L);
    }
} 