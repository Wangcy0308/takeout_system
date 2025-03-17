package org.example.controller;

import org.example.entity.Product;
import org.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "http://localhost:8081")
public class ProductController {

    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity handleOptionsRequest() {
        return ResponseEntity.ok().build();
    }

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> findAll() {
        return productService.findAll();
    }

    @GetMapping("/category/{categoryId}")
    public List<Product> findByCategory(@PathVariable Long categoryId) {
        return productService.findByCategoryId(categoryId);
    }

    @PostMapping
    public Product save(@RequestBody Product product) {
        return productService.save(product);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        productService.deleteById(id);
    }

    @GetMapping("/search")
    public List<Product> searchProductsByKeyword(@RequestParam String keyword){  //接收 请求参数（通常是 URL 中的 ?key=value 格式参数
        return productService.findByNameContaining(keyword);
    }
}
