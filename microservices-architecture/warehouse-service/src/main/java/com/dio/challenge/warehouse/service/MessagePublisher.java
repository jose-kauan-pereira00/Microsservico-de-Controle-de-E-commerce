package com.dio.challenge.warehouse.service;

import com.dio.challenge.warehouse.config.RabbitMQConfig;
import com.dio.challenge.warehouse.dto.LowStockAlertMessage;
import com.dio.challenge.warehouse.dto.StockUpdateMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessagePublisher {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void publishStockUpdate(StockUpdateMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.WAREHOUSE_EXCHANGE,
                RabbitMQConfig.STOCK_UPDATE_ROUTING_KEY,
                message
        );
        System.out.println("Published stock update message: " + message.getProductName());
    }
    
    public void publishLowStockAlert(LowStockAlertMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.WAREHOUSE_EXCHANGE,
                RabbitMQConfig.LOW_STOCK_ALERT_ROUTING_KEY,
                message
        );
        System.out.println("Published low stock alert: " + message.getProductName());
    }
}
