package org.example.service;

import org.example.entity.CartItem;
import org.example.entity.Product;
import org.example.repository.CartRepository;
import org.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    //添加商品到购物车
    public CartItem addToCart(Long userId, Long productId, Integer quantity){
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new RuntimeException("Product not found"));
        if(quantity>product.getStock()){
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }

        // 检查购物车中是否已有该商品
        CartItem cartItem = cartRepository.findByUserIdAndProductId(userId, productId);
        if(cartItem!=null){
            cartItem.setQuantity(cartItem.getQuantity()+quantity);
        }else{
            // 添加到购物车
            cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setQuantity(quantity);
        }

        return cartRepository.save(cartItem);
    }

    public List<CartItem> getCart(Long userId){
        return cartRepository.findByUserId(userId);
    }

    public CartItem updateCart(Long userId, Long productId, Integer quantity){
        CartItem cartItem = cartRepository.findByUserIdAndProductId(userId, productId);
        if(cartItem==null){
            throw new RuntimeException("Cart item not found");
        }
        cartItem.setQuantity(quantity);
        return cartRepository.save(cartItem);
    }

    public void removeCartItem(Long userId, Long productId){
        CartItem cartItem = cartRepository.findByUserIdAndProductId(userId,productId);
        if(cartItem==null){
            throw new RuntimeException("Cart item not found");
        }
        cartRepository.delete(cartItem);
    }
}
