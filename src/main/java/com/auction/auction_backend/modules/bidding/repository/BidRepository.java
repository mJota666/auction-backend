package com.auction.auction_backend.modules.bidding.repository;

import com.auction.auction_backend.modules.bidding.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Long> {
}
