package org.example.repository;

import org.example.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // 根据分类 ID 查询商品
    List<Product> findByCategoryId(Long categoryId);

    // 根据名称模糊查询商品
    List<Product> findByNameContaining(String keyword);
}
