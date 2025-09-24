package com.dio.challenge.storefront.service;

import com.dio.challenge.storefront.config.RabbitMQConfig;
import com.dio.challenge.storefront.dto.OrderCreatedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessagePublisher {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void publishOrderCreated(OrderCreatedMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_CREATED_ROUTING_KEY,
                message
        );
        System.out.println("Published order created message: Order ID " + message.getOrderId());
    }
    
    public void publishOrderStatusUpdate(Long orderId, String oldStatus, String newStatus) {
        String message = String.format("Order %d status changed from %s to %s", orderId, oldStatus, newStatus);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_STATUS_UPDATE_ROUTING_KEY,
                message
        );
        System.out.println("Published order status update: " + message);
    }
}
