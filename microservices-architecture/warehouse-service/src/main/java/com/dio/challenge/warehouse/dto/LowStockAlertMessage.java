package com.dio.challenge.warehouse.dto;

import java.time.LocalDateTime;

public class LowStockAlertMessage {
    
    private Long productId;
    private String productName;
    private Integer currentStock;
    private Integer threshold;
    private LocalDateTime timestamp;
    
    // Constructors
    public LowStockAlertMessage() {}
    
    public LowStockAlertMessage(Long productId, String productName, Integer currentStock, Integer threshold) {
        this.productId = productId;
        this.productName = productName;
        this.currentStock = currentStock;
        this.threshold = threshold;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public Integer getCurrentStock() {
        return currentStock;
    }
    
    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }
    
    public Integer getThreshold() {
        return threshold;
    }
    
    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
