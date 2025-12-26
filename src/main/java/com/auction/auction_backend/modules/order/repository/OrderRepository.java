package com.auction.auction_backend.modules.order.repository;

import com.auction.auction_backend.modules.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
