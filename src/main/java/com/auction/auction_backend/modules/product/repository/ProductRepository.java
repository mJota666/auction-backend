package com.auction.auction_backend.modules.product.repository;

import com.auction.auction_backend.common.enums.ProductStatus;
import com.auction.auction_backend.modules.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);
}
