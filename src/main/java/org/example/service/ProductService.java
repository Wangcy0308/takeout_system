package org.example.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.example.entity.Product;
import org.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @RateLimiter(name = "productLimiter")
    @CircuitBreaker(name = "productBreaker", fallbackMethod = "fallbackFindAll")
    public List<Product> findAll(){
        return productRepository.findAll();
    }

    @RateLimiter(name = "productLimiter")
    @CircuitBreaker(name = "productBreaker", fallbackMethod = "fallbackByCategory")
    public List<Product> findByCategoryId(Long categoryId){
        return productRepository.findByCategoryId(categoryId);
    }

    @RateLimiter(name = "productLimiter")
    @CircuitBreaker(name = "productBreaker", fallbackMethod = "fallbackByName")
    public List<Product> findByNameContaining(String keyword){
        return productRepository.findByNameContaining(keyword);
    }

    @CircuitBreaker(name = "productBreaker", fallbackMethod = "fallbackSave")
    public Product save(Product product) {
        System.out.println("Category: " + product.getCategory());
        if (product.getCategory() == null || product.getCategory().getId() == null) {
            throw new RuntimeException("Category is required");
        }
        return productRepository.save(product);
    }

    @CircuitBreaker(name = "productBreaker", fallbackMethod = "fallbackDelete")
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    // -------- fallback methods --------
    public List<Product> fallbackFindAll(Throwable t) {
        return Collections.emptyList();
    }

    public List<Product> fallbackByCategory(Long categoryId, Throwable t) {
        return Collections.emptyList();
    }

    public List<Product> fallbackByName(String keyword, Throwable t) {
        return Collections.emptyList();
    }

    public Product fallbackSave(Product product, Throwable t) {
        return null; // 可替换为默认对象或错误响应包装类
    }

    public void fallbackDelete(Long id, Throwable t) {
        // 可记录日志或通知系统管理员
        System.out.println("Delete fallback triggered for ID: " + id);
    }
}
