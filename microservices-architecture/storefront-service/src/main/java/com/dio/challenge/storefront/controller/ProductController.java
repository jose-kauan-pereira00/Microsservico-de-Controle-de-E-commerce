package com.dio.challenge.storefront.controller;

import com.dio.challenge.storefront.dto.ProductDTO;
import com.dio.challenge.storefront.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/storefront/products")
@CrossOrigin(origins = "*")
public class ProductController {
    
    @Autowired
    private WarehouseService warehouseService;
    
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = warehouseService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO product = warehouseService.getProductById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<ProductDTO>> getAvailableProducts() {
        List<ProductDTO> products = warehouseService.getAvailableProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String name) {
        List<ProductDTO> products = warehouseService.searchProducts(name);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}/stock-check")
    public ResponseEntity<Boolean> checkStockAvailability(@PathVariable Long id, @RequestParam Integer quantity) {
        boolean available = warehouseService.checkStockAvailability(id, quantity);
        return ResponseEntity.ok(available);
    }
}
