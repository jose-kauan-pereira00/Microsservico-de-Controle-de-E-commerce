package com.dio.challenge.storefront.service;

import com.dio.challenge.storefront.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class WarehouseService {
    
    @Autowired
    private WebClient warehouseWebClient;
    
    public List<ProductDTO> getAllProducts() {
        return warehouseWebClient
                .get()
                .uri("/api/products")
                .retrieve()
                .bodyToFlux(ProductDTO.class)
                .collectList()
                .block();
    }
    
    public ProductDTO getProductById(Long productId) {
        return warehouseWebClient
                .get()
                .uri("/api/products/{id}", productId)
                .retrieve()
                .bodyToMono(ProductDTO.class)
                .block();
    }
    
    public List<ProductDTO> getAvailableProducts() {
        return warehouseWebClient
                .get()
                .uri("/api/products/available")
                .retrieve()
                .bodyToFlux(ProductDTO.class)
                .collectList()
                .block();
    }
    
    public List<ProductDTO> searchProducts(String name) {
        return warehouseWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/products/search")
                        .queryParam("name", name)
                        .build())
                .retrieve()
                .bodyToFlux(ProductDTO.class)
                .collectList()
                .block();
    }
    
    public boolean checkStockAvailability(Long productId, Integer quantity) {
        return warehouseWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/products/{id}/stock-check")
                        .queryParam("quantity", quantity)
                        .build(productId))
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }
    
    public void updateStock(Long productId, Integer quantity, String operation) {
        StockUpdateRequest request = new StockUpdateRequest(productId, quantity, operation);
        
        warehouseWebClient
                .put()
                .uri("/api/products/stock")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ProductDTO.class)
                .block();
    }
    
    // Inner class for stock update request
    public static class StockUpdateRequest {
        private Long productId;
        private Integer quantity;
        private String operation;
        
        public StockUpdateRequest() {}
        
        public StockUpdateRequest(Long productId, Integer quantity, String operation) {
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
}
