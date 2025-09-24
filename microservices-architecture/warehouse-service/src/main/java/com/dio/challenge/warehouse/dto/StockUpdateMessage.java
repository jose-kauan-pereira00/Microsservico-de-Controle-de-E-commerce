package com.dio.challenge.warehouse.dto;

import java.time.LocalDateTime;

public class StockUpdateMessage {
    
    private Long productId;
    private String productName;
    private Integer previousStock;
    private Integer currentStock;
    private Integer quantityChanged;
    private String operation;
    private LocalDateTime timestamp;
    
    // Constructors
    public StockUpdateMessage() {}
    
    public StockUpdateMessage(Long productId, String productName, Integer previousStock, 
                             Integer currentStock, Integer quantityChanged, String operation) {
        this.productId = productId;
        this.productName = productName;
        this.previousStock = previousStock;
        this.currentStock = currentStock;
        this.quantityChanged = quantityChanged;
        this.operation = operation;
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
    
    public Integer getPreviousStock() {
        return previousStock;
    }
    
    public void setPreviousStock(Integer previousStock) {
        this.previousStock = previousStock;
    }
    
    public Integer getCurrentStock() {
        return currentStock;
    }
    
    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }
    
    public Integer getQuantityChanged() {
        return quantityChanged;
    }
    
    public void setQuantityChanged(Integer quantityChanged) {
        this.quantityChanged = quantityChanged;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
