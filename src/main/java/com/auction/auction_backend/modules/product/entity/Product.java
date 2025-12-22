package com.auction.auction_backend.modules.product.entity;

import com.auction.auction_backend.common.enums.ProductStatus;
import com.auction.auction_backend.common.persistence.entity.BaseEntity;
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
    @Column(nullable = false)
    private String title;

    @Column(name = "title_normalized")
    private String titleNormalized;

    @Column(name = "description_html", columnDefinition = "LONGTEXT")
    private String description;

    @Column(name = "start_price", nullable = false)
    private BigDecimal startPrice;

    @Column(name = "step_price", nullable = false)
    private BigDecimal stepPrice;

    @Column(name = "buy_now_price")
    private BigDecimal buyNowPrice;

    @Column(name = "current_price")
    private BigDecimal currentPrice;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_winner_id")
    private User winner;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();
}
