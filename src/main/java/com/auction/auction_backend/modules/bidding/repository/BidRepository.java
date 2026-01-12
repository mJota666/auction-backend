package com.auction.auction_backend.modules.bidding.repository;

import com.auction.auction_backend.modules.bidding.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BidRepository extends JpaRepository<Bid, Long> {
    Page<Bid> findByBidderId(Long bidderId, Pageable pageable);

    Page<Bid> findByProductId(Long productId, Pageable pageable);

    void deleteByProductAndBidder(com.auction.auction_backend.modules.product.entity.Product product,
            com.auction.auction_backend.modules.user.entity.User bidder);

    void deleteByBidderId(Long bidderId);

    boolean existsByProductAndBidderAndMaxAmount(com.auction.auction_backend.modules.product.entity.Product product,
            com.auction.auction_backend.modules.user.entity.User bidder, java.math.BigDecimal maxAmount);
}
