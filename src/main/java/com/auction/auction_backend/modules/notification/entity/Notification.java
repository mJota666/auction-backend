package com.auction.auction_backend.modules.notification.entity;

import com.auction.auction_backend.common.persistence.entity.BaseEntity;
import com.auction.auction_backend.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notifications")

public class Notification extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    @Column(name = "is_read")
    @Builder.Default
    private boolean isRead = false;

    @Column(name = "related_url")
    private String relatedUrl;

    @Column(name = "type")
    private String type;
}
