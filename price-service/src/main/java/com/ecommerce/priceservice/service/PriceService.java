package com.ecommerce.priceservice.service;

import com.ecommerce.priceservice.entity.Price;
import com.ecommerce.priceservice.exception.PriceNotFoundException;
import com.ecommerce.priceservice.repository.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PriceService {

    @Autowired
    private PriceRepository priceRepository;

    public Price addPrice(Price price) {
        return priceRepository.save(price);
    }

    public Price updatePrice(Long productId, Price updatedPrice) {
        Price existing = priceRepository.findByProductId(productId)
                .orElseThrow(() -> new PriceNotFoundException(
                        "Price not found for productId: " + productId));
        existing.setPrice(updatedPrice.getPrice());
        existing.setDiscount(updatedPrice.getDiscount());
        return priceRepository.save(existing);
    }

    public Price getByProductId(Long productId) {
        return priceRepository.findByProductId(productId)
                .orElseThrow(() -> new PriceNotFoundException(
                        "Price not found for productId: " + productId));
    }
}