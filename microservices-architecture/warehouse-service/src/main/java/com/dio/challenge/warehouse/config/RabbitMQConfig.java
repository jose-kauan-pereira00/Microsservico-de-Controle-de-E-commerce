package com.dio.challenge.warehouse.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    // Queue names
    public static final String STOCK_UPDATE_QUEUE = "stock.update.queue";
    public static final String LOW_STOCK_ALERT_QUEUE = "low.stock.alert.queue";
    public static final String ORDER_PROCESSING_QUEUE = "order.processing.queue";
    
    // Exchange names
    public static final String WAREHOUSE_EXCHANGE = "warehouse.exchange";
    public static final String ORDER_EXCHANGE = "order.exchange";
    
    // Routing keys
    public static final String STOCK_UPDATE_ROUTING_KEY = "stock.update";
    public static final String LOW_STOCK_ALERT_ROUTING_KEY = "stock.low";
    public static final String ORDER_PROCESSING_ROUTING_KEY = "order.processing";
    
    // Exchanges
    @Bean
    public TopicExchange warehouseExchange() {
        return new TopicExchange(WAREHOUSE_EXCHANGE);
    }
    
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }
    
    // Queues
    @Bean
    public Queue stockUpdateQueue() {
        return QueueBuilder.durable(STOCK_UPDATE_QUEUE).build();
    }
    
    @Bean
    public Queue lowStockAlertQueue() {
        return QueueBuilder.durable(LOW_STOCK_ALERT_QUEUE).build();
    }
    
    @Bean
    public Queue orderProcessingQueue() {
        return QueueBuilder.durable(ORDER_PROCESSING_QUEUE).build();
    }
    
    // Bindings
    @Bean
    public Binding stockUpdateBinding() {
        return BindingBuilder
                .bind(stockUpdateQueue())
                .to(warehouseExchange())
                .with(STOCK_UPDATE_ROUTING_KEY);
    }
    
    @Bean
    public Binding lowStockAlertBinding() {
        return BindingBuilder
                .bind(lowStockAlertQueue())
                .to(warehouseExchange())
                .with(LOW_STOCK_ALERT_ROUTING_KEY);
    }
    
    @Bean
    public Binding orderProcessingBinding() {
        return BindingBuilder
                .bind(orderProcessingQueue())
                .to(orderExchange())
                .with(ORDER_PROCESSING_ROUTING_KEY);
    }
    
    // Message converter
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    // RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
