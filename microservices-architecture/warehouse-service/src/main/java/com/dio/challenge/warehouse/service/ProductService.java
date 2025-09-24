package com.dio.challenge.warehouse.service;

import com.dio.challenge.warehouse.dto.ProductDTO;
import com.dio.challenge.warehouse.dto.StockUpdateDTO;
import com.dio.challenge.warehouse.model.Product;
import com.dio.challenge.warehouse.repository.ProductRepository;
import com.dio.challenge.warehouse.rabbitmq.MessagePublisher;
import com.dio.challenge.warehouse.rabbitmq.StockUpdateMessage;
import com.dio.challenge.warehouse.rabbitmq.LowStockAlertMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private MessagePublisher messagePublisher;
    
    private static final Integer LOW_STOCK_THRESHOLD = 10;
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public List<Product> getAvailableProducts() {
        return productRepository.findAvailableProducts();
    }
    
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
    
    public Product createProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStockQuantity(productDTO.getStockQuantity());
        
        return productRepository.save(product);
    }
    
    public Optional<Product> updateProduct(Long id, ProductDTO productDTO) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(productDTO.getName());
                    product.setDescription(productDTO.getDescription());
                    product.setPrice(productDTO.getPrice());
                    product.setStockQuantity(productDTO.getStockQuantity());
                    return productRepository.save(product);
                });
    }
    
    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public Optional<Product> updateStock(StockUpdateDTO stockUpdateDTO) {
        return productRepository.findById(stockUpdateDTO.getProductId())
                .map(product -> {
                    int previousStock = product.getStockQuantity();
                    int currentStock;
                    
                    if ("ADD".equalsIgnoreCase(stockUpdateDTO.getOperation())) {
                        currentStock = previousStock + stockUpdateDTO.getQuantity();
                    } else if ("SUBTRACT".equalsIgnoreCase(stockUpdateDTO.getOperation())) {
                        currentStock = Math.max(0, previousStock - stockUpdateDTO.getQuantity());
                    } else {
                        throw new IllegalArgumentException("Invalid operation. Use 'ADD' or 'SUBTRACT'");
                    }
                    
                    product.setStockQuantity(currentStock);
                    Product savedProduct = productRepository.save(product);
                    
                    StockUpdateMessage stockMessage = new StockUpdateMessage(
                            product.getId(),
                            product.getName(),
                            previousStock,
                            currentStock,
                            stockUpdateDTO.getQuantity(),
                            stockUpdateDTO.getOperation()
                    );
                    messagePublisher.publishStockUpdate(stockMessage);
                    
                    if (currentStock <= LOW_STOCK_THRESHOLD) {
                        LowStockAlertMessage alertMessage = new LowStockAlertMessage(
                                product.getId(),
                                product.getName(),
                                currentStock,
                                LOW_STOCK_THRESHOLD
                        );
                        messagePublisher.publishLowStockAlert(alertMessage);
                    }
                    
                    return savedProduct;
                });
    }
    
    public boolean checkStockAvailability(Long productId, Integer requiredQuantity) {
        return productRepository.findById(productId)
                .map(product -> product.getStockQuantity() >= requiredQuantity)
                .orElse(false);
    }
    
    public List<Product> getLowStockProducts(Integer threshold) {
        return productRepository.findLowStockProducts(threshold);
    }
}
