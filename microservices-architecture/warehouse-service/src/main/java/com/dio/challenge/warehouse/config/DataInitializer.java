package com.dio.challenge.warehouse.config;

import com.dio.challenge.warehouse.model.Product;
import com.dio.challenge.warehouse.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            // Initialize with sample products
            productRepository.save(new Product("Smartphone Samsung Galaxy", "Latest Samsung smartphone with advanced features", new BigDecimal("899.99"), 50));
            productRepository.save(new Product("Notebook Dell Inspiron", "High-performance laptop for work and gaming", new BigDecimal("1299.99"), 25));
            productRepository.save(new Product("Headphones Sony WH-1000XM4", "Noise-canceling wireless headphones", new BigDecimal("349.99"), 100));
            productRepository.save(new Product("Smart TV LG 55\"", "4K Ultra HD Smart TV with webOS", new BigDecimal("799.99"), 15));
            productRepository.save(new Product("Gaming Mouse Logitech", "High-precision gaming mouse with RGB lighting", new BigDecimal("79.99"), 200));
            productRepository.save(new Product("Mechanical Keyboard", "RGB mechanical keyboard for gaming and typing", new BigDecimal("129.99"), 75));
            productRepository.save(new Product("Wireless Charger", "Fast wireless charging pad for smartphones", new BigDecimal("39.99"), 150));
            productRepository.save(new Product("Bluetooth Speaker", "Portable Bluetooth speaker with excellent sound quality", new BigDecimal("89.99"), 80));
            
            System.out.println("Sample products initialized in warehouse database");
        }
    }
}
