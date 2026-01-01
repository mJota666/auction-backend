package com.auction.auction_backend.modules.user.entity;

import com.auction.auction_backend.common.enums.UpgradeRequestStatus;
import com.auction.auction_backend.common.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "upgrade_requests")
public class UpgradeRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UpgradeRequestStatus status = UpgradeRequestStatus.PENDING;

    @Column(name = "admin_note")
    private String adminNote;
}
