package com.dio.challenge.warehouse.dto;

import jakarta.validation.constraints.NotNull;

public class StockUpdateDTO {
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    @NotNull(message = "Quantity is required")
    private Integer quantity;
    
    private String operation; // "ADD" or "SUBTRACT"
    
    // Constructors
    public StockUpdateDTO() {}
    
    public StockUpdateDTO(Long productId, Integer quantity, String operation) {
        this.productId = productId;
        this.quantity = quantity;
        this.operation = operation;
    }
    
    // Getters and Setters
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
}
