package com.auction.auction_backend.modules.bidding.repository;

import com.auction.auction_backend.modules.bidding.entity.BlockedBidder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedBidderRepository extends JpaRepository<BlockedBidder, Long> {
    boolean existsByProductIdAndUserId(Long productId, Long userId);
}
