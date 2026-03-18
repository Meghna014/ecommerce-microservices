package com.ecommerce.priceservice.repository;

import com.ecommerce.priceservice.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {
    Optional<Price> findByProductId(Long productId);
}