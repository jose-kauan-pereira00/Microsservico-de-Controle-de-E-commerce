package com.dio.challenge.storefront.service;

import com.dio.challenge.storefront.dto.OrderCreatedMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MessageListener {
    
    @RabbitListener(queues = "stock.update.queue")
    public void handleStockUpdate(Object stockUpdateMessage) {
        System.out.println("Received stock update in storefront: " + stockUpdateMessage);
        // Process stock update - could update local cache, notify customers, etc.
    }
    
    @RabbitListener(queues = "low.stock.alert.queue")
    public void handleLowStockAlert(Object lowStockAlertMessage) {
        System.out.println("Received low stock alert in storefront: " + lowStockAlertMessage);
        // Process low stock alert - could notify customers, remove from featured products, etc.
    }
}
