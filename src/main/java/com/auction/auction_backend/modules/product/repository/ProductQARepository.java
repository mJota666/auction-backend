package com.auction.auction_backend.modules.product.repository;

import com.auction.auction_backend.modules.product.entity.ProductQA;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductQARepository extends JpaRepository<ProductQA, Long> {
    Page<ProductQA> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);
}
