package com.auction.auction_backend.modules.user.entity;

import com.auction.auction_backend.common.persistence.entity.BaseEntity;
import com.auction.auction_backend.modules.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ratings")
public class Rating extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rater_id", nullable = false)
    private User rater; // Người đánh giá

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rated_user_id", nullable = false)
    private User ratedUser; // Người được đánh giá

    @Column(nullable = false)
    private int score; // +1 hoặc -1

    @Column(columnDefinition = "TEXT")
    private String comment; // Nhận xét
}
