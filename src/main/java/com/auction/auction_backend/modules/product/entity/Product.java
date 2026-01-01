package com.auction.auction_backend.modules.product.entity;

import com.auction.auction_backend.common.enums.ProductStatus;
import com.auction.auction_backend.common.persistence.entity.BaseEntity;
import com.auction.auction_backend.modules.bidding.entity.Bid;
import com.auction.auction_backend.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Product extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String title;

    @Column(name = "title_normalized", nullable = false)
    private String titleNormalized;

    @Column(name = "description_html", nullable = false, columnDefinition = "LONGTEXT")
    private String description;

    @Column(name = "description_append_log", columnDefinition = "LONGTEXT")
    private String descriptionAppendLog;

    @Column(name = "start_price", nullable = false)
    private BigDecimal startPrice;

    @Column(name = "step_price", nullable = false)
    private BigDecimal stepPrice;

    @Column(name = "buy_now_price")
    private BigDecimal buyNowPrice;

    @Column(name = "current_price", nullable = false)
    private BigDecimal currentPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_winner_id")
    private User currentWinner;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "allow_unrated_bidder", nullable = false)
    private boolean allowUnratedBidder = false;

    @Column(name = "auto_extend_enabled", nullable = false)
    private boolean autoExtendEnabled = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Bid> bids;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images;
}
