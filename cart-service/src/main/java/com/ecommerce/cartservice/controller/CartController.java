package com.ecommerce.cartservice.controller;

import com.ecommerce.cartservice.entity.Cart;
import com.ecommerce.cartservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(
            @RequestParam Long customerId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        return new ResponseEntity<>(
                cartService.addToCart(customerId, productId, quantity),
                HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Cart> getCart(@PathVariable Long customerId) {
        return ResponseEntity.ok(cartService.getCart(customerId));
    }

    @DeleteMapping("/{customerId}/remove/{productId}")
    public ResponseEntity<String> removeItem(
            @PathVariable Long customerId,
            @PathVariable Long productId) {
        cartService.removeItem(customerId, productId);
        return ResponseEntity.ok("Item removed from cart!");
    }

    @DeleteMapping("/{customerId}/clear")
    public ResponseEntity<String> clearCart(@PathVariable Long customerId) {
        cartService.clearCart(customerId);
        return ResponseEntity.ok("Cart cleared!");
    }
}