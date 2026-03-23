package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.ProductResponseDto;
import com.ecommerce.productservice.entity.Product;
import com.ecommerce.productservice.exception.ProductNotFoundException;
import com.ecommerce.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        existing.setName(updatedProduct.getName());
        existing.setCategory(updatedProduct.getCategory());
        existing.setDescription(updatedProduct.getDescription());
        existing.setSize(updatedProduct.getSize());
        existing.setDesign(updatedProduct.getDesign());
        existing.setMaterial(updatedProduct.getMaterial());
        existing.setStock(updatedProduct.getStock());
        return productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return buildProductResponse(product);
    }

    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::buildProductResponse)
                .collect(Collectors.toList());
    }

    public void reduceStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Insufficient stock for productId: " + productId);
        }
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    private ProductResponseDto buildProductResponse(Product product) {
        ProductResponseDto response = new ProductResponseDto();
        response.setProductId(product.getId());
        response.setName(product.getName());
        response.setCategory(product.getCategory());
        response.setDescription(product.getDescription());
        response.setSize(product.getSize());
        response.setDesign(product.getDesign());
        response.setMaterial(product.getMaterial());
        response.setStock(product.getStock());

        //Call price service with Circuit Breaker
        try {
            Map<String, Object> priceData = getPriceWithCircuitBreaker(product.getId());
            if (priceData != null) {
                response.setPrice(priceData.get("price") != null ?
                        Double.valueOf(priceData.get("price").toString()) : null);
                response.setDiscount(priceData.get("discount") != null ?
                        Double.valueOf(priceData.get("discount").toString()) : null);
            }
        }catch (Exception e)
        {
            System.out.println("Price unavailable for product: " + product.getId());
            response.setPrice(null);
            response.setDiscount(null);
        }
        return response;
    }


    @CircuitBreaker(name = "priceService", fallbackMethod = "getPriceFallback")
    public Map<String, Object> getPriceWithCircuitBreaker(Long productId) {
        Map<String, Object> price = restTemplate.getForObject(
                "http://price-service/price/" + productId, Map.class);
        return price;
    }

    // Fallback method
    public Map<String, Object> getPriceFallback(Long productId, Throwable ex) {
        System.out.println("Fallback triggered! Reason: " + ex.getMessage());
        return null;
    }
}