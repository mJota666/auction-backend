package com.auction.auction_backend.modules.order.repository;

import com.auction.auction_backend.modules.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByWinnerId(Long winnerId);

    List<Order> findBySellerId(Long sellerId);

    Optional<Order> findByProductId(Long productId);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(o.finalPrice) FROM Order o WHERE o.status = 'PAID' OR o.status = 'DELIVERED'")
    java.math.BigDecimal calculateTotalRevenue();
}
