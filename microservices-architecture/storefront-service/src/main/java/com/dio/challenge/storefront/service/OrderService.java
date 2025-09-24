package com.dio.challenge.storefront.service;

import com.dio.challenge.storefront.dto.OrderDTO;
import com.dio.challenge.storefront.dto.OrderItemDTO;
import com.dio.challenge.storefront.dto.ProductDTO;
import com.dio.challenge.storefront.model.Order;
import com.dio.challenge.storefront.model.OrderItem;
import com.dio.challenge.storefront.model.OrderStatus;
import com.dio.challenge.storefront.repository.OrderRepository;
import com.dio.challenge.storefront.messaging.MessagePublisher;
import com.dio.challenge.storefront.messaging.OrderCreatedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private WarehouseService warehouseService;
    
    @Autowired
    private MessagePublisher messagePublisher;
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    public List<Order> getOrdersByCustomerEmail(String customerEmail) {
        return orderRepository.findByCustomerEmailOrderByCreatedAtDesc(customerEmail);
    }
    
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status);
    }
    
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findOrdersByDateRange(startDate, endDate);
    }
    
    public Order createOrder(OrderDTO orderDTO) {
        // Validate stock availability for all items
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            boolean stockAvailable = warehouseService.checkStockAvailability(itemDTO.getProductId(), itemDTO.getQuantity());
            if (!stockAvailable) {
                throw new RuntimeException("Insufficient stock for product ID: " + itemDTO.getProductId());
            }
        }
        
        // Create order
        Order order = new Order();
        order.setCustomerName(orderDTO.getCustomerName());
        order.setCustomerEmail(orderDTO.getCustomerEmail());
        order.setStatus(OrderStatus.PENDING);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        // Create order items
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            ProductDTO product = warehouseService.getProductById(itemDTO.getProductId());
            if (product == null) {
                throw new RuntimeException("Product not found: " + itemDTO.getProductId());
            }
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
            
            order.getItems().add(orderItem);
            totalAmount = totalAmount.add(orderItem.getTotalPrice());
        }
        
        order.setTotalAmount(totalAmount);
        
        // Save order
        Order savedOrder = orderRepository.save(order);
        
        // Update stock in warehouse
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            warehouseService.updateStock(itemDTO.getProductId(), itemDTO.getQuantity(), "SUBTRACT");
        }
        
        List<OrderCreatedMessage.OrderItemMessage> messageItems = savedOrder.getItems().stream()
                .map(item -> new OrderCreatedMessage.OrderItemMessage(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice()
                ))
                .toList();
        
        OrderCreatedMessage orderMessage = new OrderCreatedMessage(
                savedOrder.getId(),
                savedOrder.getCustomerName(),
                savedOrder.getCustomerEmail(),
                savedOrder.getTotalAmount(),
                messageItems
        );
        messagePublisher.publishOrderCreated(orderMessage);
        
        return savedOrder;
    }
    
    public Optional<Order> updateOrderStatus(Long orderId, OrderStatus newStatus) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    OrderStatus oldStatus = order.getStatus();
                    order.setStatus(newStatus);
                    Order savedOrder = orderRepository.save(order);
                    
                    messagePublisher.publishOrderStatusUpdate(orderId, oldStatus.toString(), newStatus.toString());
                    
                    return savedOrder;
                });
    }
    
    public boolean cancelOrder(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.CONFIRMED) {
                // Restore stock
                for (OrderItem item : order.getItems()) {
                    warehouseService.updateStock(item.getProductId(), item.getQuantity(), "ADD");
                }
                
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                return true;
            }
        }
        return false;
    }
    
    public Long getOrderCountByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }
}
