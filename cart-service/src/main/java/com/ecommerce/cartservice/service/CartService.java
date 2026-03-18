package com.ecommerce.cartservice.service;

import com.ecommerce.cartservice.entity.Cart;
import com.ecommerce.cartservice.entity.CartItem;
import com.ecommerce.cartservice.exception.CartNotFoundException;
import com.ecommerce.cartservice.repository.CartItemRepository;
import com.ecommerce.cartservice.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    // Get or create cart for customer
    private Cart getOrCreateCart(Long customerId) {
        return cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setCustomerId(customerId);
                    cart.setStatus("ACTIVE");
                    return cartRepository.save(cart);
                });
    }

    public Cart addToCart(Long customerId, Long productId, Integer quantity) {
        Cart cart = getOrCreateCart(customerId);

        // Check if item already exists
        cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .ifPresentOrElse(
                        existing -> {
                            existing.setQuantity(existing.getQuantity() + quantity);
                            cartItemRepository.save(existing);
                        },
                        () -> {
                            CartItem item = new CartItem();
                            item.setProductId(productId);
                            item.setQuantity(quantity);
                            item.setCart(cart);
                            cartItemRepository.save(item);
                        }
                );

        return cartRepository.findByCustomerId(customerId).get();
    }

    public Cart getCart(Long customerId) {
        return cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CartNotFoundException(
                        "Cart not found for customerId: " + customerId));
    }

    public void removeItem(Long customerId, Long productId) {
        Cart cart = getCart(customerId);
        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new CartNotFoundException(
                        "Item not found in cart for productId: " + productId));
        cartItemRepository.delete(item);
    }

    @Transactional
    public void clearCart(Long customerId) {
        Cart cart = getCart(customerId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}