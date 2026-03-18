package com.ecommerce.priceservice.controller;

import com.ecommerce.priceservice.entity.Price;
import com.ecommerce.priceservice.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/price")
public class PriceController {

    @Autowired
    private PriceService priceService;

    @PostMapping
    public ResponseEntity<Price> addPrice(@RequestBody Price price) {
        return new ResponseEntity<>(priceService.addPrice(price), HttpStatus.CREATED);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Price> updatePrice(
            @PathVariable Long productId,
            @RequestBody Price price) {
        return ResponseEntity.ok(priceService.updatePrice(productId, price));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Price> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(priceService.getByProductId(productId));
    }
}