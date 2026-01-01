package com.auction.auction_backend.modules.user.dto.response;

import com.auction.auction_backend.common.enums.UpgradeRequestStatus;
import com.auction.auction_backend.modules.user.entity.UpgradeRequest;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UpgradeRequestResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String reason;
    private UpgradeRequestStatus status;
    private String adminNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UpgradeRequestResponse fromEntity(UpgradeRequest request) {
        return UpgradeRequestResponse.builder()
                .id(request.getId())
                .userId(request.getUser().getId())
                .userName(request.getUser().getFullName())
                .reason(request.getReason())
                .status(request.getStatus())
                .adminNote(request.getAdminNote())
                .createdAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}
