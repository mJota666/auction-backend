package com.auction.auction_backend.modules.bidding.entity;

import com.auction.auction_backend.common.enums.BidType;
import com.auction.auction_backend.common.persistence.entity.BaseEntity;
import com.auction.auction_backend.modules.product.entity.Product;
import com.auction.auction_backend.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bids")
public class Bid extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_id", nullable = false)
    private User bidder;

    @Column(name = "bid_amount", nullable = false)
    private BigDecimal bidAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "bid_type", nullable = false)
    @Builder.Default
    private BidType bidType = BidType.AUTO;

    @Column(name = "max_amount")
    private BigDecimal maxAmount;
}
