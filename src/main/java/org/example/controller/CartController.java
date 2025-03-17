package org.example.controller;


import org.example.entity.CartItem;
import org.example.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/cart")

public class CartController {
    @Autowired //通过 @Autowired，Spring 自动将 OrderService 注入到 OrderController 中
    private CartService cartService;

    //添加商品到购物车
    @PostMapping("/add")
    public CartItem addToCart(@RequestParam Long userId, @RequestParam Long productId, @RequestParam Integer quantity) {
        return cartService.addToCart(userId, productId, quantity);
    }

    @GetMapping
    public List<CartItem> getCart(@RequestParam Long userId){
        return cartService.getCart(userId);
    }

    @PutMapping("/update")
    public CartItem updateCart(@RequestParam Long userId,  @RequestParam Long productId, @RequestParam Integer quantity){
        return cartService.updateCart(userId,productId,quantity);
    }

    @DeleteMapping("/remove")
    public String removeCartItem(@RequestParam Long userId, @RequestParam Long productId){
        cartService.removeCartItem(userId,productId);
        return "Cart item removed successfully";
    }
}
