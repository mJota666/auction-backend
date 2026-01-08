package com.auction.auction_backend.modules.bidding.entity;

import com.auction.auction_backend.common.persistence.entity.BaseEntity;
import com.auction.auction_backend.modules.product.entity.Product;
import com.auction.auction_backend.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "blocked_bidder_list", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "product_id", "user_id" })
})
public class BlockedBidder extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
