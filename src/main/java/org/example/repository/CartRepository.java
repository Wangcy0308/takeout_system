package org.example.repository;

import org.example.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long>{
    List<CartItem> findByUserId(Long userID);

    CartItem findByUserIdAndProductId(Long userId, Long productId);
}