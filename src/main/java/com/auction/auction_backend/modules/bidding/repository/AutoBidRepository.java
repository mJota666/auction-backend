package com.auction.auction_backend.modules.bidding.repository;

import com.auction.auction_backend.modules.bidding.entity.AutoBidMax;
import com.auction.auction_backend.modules.product.entity.Product;
import com.auction.auction_backend.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutoBidRepository extends JpaRepository<AutoBidMax, Long> {
    @Query("SELECT a FROM AutoBidMax a " +
            "WHERE a.product.id = :productId " +
            "ORDER BY a.maxAmount DESC, a.createdAt ASC")
    List<AutoBidMax> findSortedBids(Long productId);

    Optional<AutoBidMax> findByProductAndBidder(Product product, User bidder);

    void deleteByProductAndBidder(Product product, User bidder);

    void deleteByBidderId(Long bidderId);
}
