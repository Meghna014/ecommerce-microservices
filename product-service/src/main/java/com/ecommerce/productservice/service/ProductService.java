package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.ProductResponseDto;
import com.ecommerce.productservice.entity.Product;
import com.ecommerce.productservice.exception.ProductNotFoundException;
import com.ecommerce.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

        // Call Price Service only
        try {
            Map price = restTemplate.getForObject(
                    "http://price-service/price/" + product.getId(), Map.class);
            if (price != null) {
                response.setPrice(price.get("price") != null ?
                        Double.valueOf(price.get("price").toString()) : null);
                response.setDiscount(price.get("discount") != null ?
                        Double.valueOf(price.get("discount").toString()) : null);
            }
        } catch (Exception e) {
            System.out.println("Price service unavailable: " + e.getMessage());
        }

        return response;
    }
}