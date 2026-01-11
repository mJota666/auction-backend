package com.auction.auction_backend.modules.product.repository;

import com.auction.auction_backend.common.enums.ProductStatus;
import com.auction.auction_backend.modules.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' AND p.endAt < :now")
    List<Product> findExpiredAuctions(LocalDateTime now);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    List<Product> findTop5ByStatusOrderByEndAtAsc(ProductStatus status);

    List<Product> findTop5ByStatusOrderByCurrentPriceDesc(ProductStatus status);

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' ORDER BY SIZE(p.bids) DESC")
    List<Product> findTopMostBidded(Pageable pageable);

    List<Product> findTop5ByCategoryIdAndIdNotAndStatus(Long categoryId, Long productId, ProductStatus status);

    long countByStatus(ProductStatus status);

    long countByCategoryId(Long categoryId);
}
