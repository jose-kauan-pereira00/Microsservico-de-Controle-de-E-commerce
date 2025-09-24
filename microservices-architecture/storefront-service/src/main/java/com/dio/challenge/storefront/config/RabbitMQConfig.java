package com.dio.challenge.storefront.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    // Queue names
    public static final String ORDER_CREATED_QUEUE = "order.created.queue";
    public static final String ORDER_STATUS_UPDATE_QUEUE = "order.status.update.queue";
    public static final String STOCK_UPDATE_QUEUE = "stock.update.queue";
    public static final String LOW_STOCK_ALERT_QUEUE = "low.stock.alert.queue";
    
    // Exchange names
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String WAREHOUSE_EXCHANGE = "warehouse.exchange";
    
    // Routing keys
    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";
    public static final String ORDER_STATUS_UPDATE_ROUTING_KEY = "order.status.update";
    public static final String STOCK_UPDATE_ROUTING_KEY = "stock.update";
    public static final String LOW_STOCK_ALERT_ROUTING_KEY = "stock.low";
    
    // Exchanges
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }
    
    @Bean
    public TopicExchange warehouseExchange() {
        return new TopicExchange(WAREHOUSE_EXCHANGE);
    }
    
    // Queues
    @Bean
    public Queue orderCreatedQueue() {
        return QueueBuilder.durable(ORDER_CREATED_QUEUE).build();
    }
    
    @Bean
    public Queue orderStatusUpdateQueue() {
        return QueueBuilder.durable(ORDER_STATUS_UPDATE_QUEUE).build();
    }
    
    @Bean
    public Queue stockUpdateQueue() {
        return QueueBuilder.durable(STOCK_UPDATE_QUEUE).build();
    }
    
    @Bean
    public Queue lowStockAlertQueue() {
        return QueueBuilder.durable(LOW_STOCK_ALERT_QUEUE).build();
    }
    
    // Bindings
    @Bean
    public Binding orderCreatedBinding() {
        return BindingBuilder
                .bind(orderCreatedQueue())
                .to(orderExchange())
                .with(ORDER_CREATED_ROUTING_KEY);
    }
    
    @Bean
    public Binding orderStatusUpdateBinding() {
        return BindingBuilder
                .bind(orderStatusUpdateQueue())
                .to(orderExchange())
                .with(ORDER_STATUS_UPDATE_ROUTING_KEY);
    }
    
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
