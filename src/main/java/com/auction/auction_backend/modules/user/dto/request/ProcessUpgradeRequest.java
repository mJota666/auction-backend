package com.auction.auction_backend.modules.user.dto.request;

import com.auction.auction_backend.common.enums.UpgradeRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProcessUpgradeRequest {
    @NotNull(message = "Trạng thái không được để trống")
    private UpgradeRequestStatus status;
    private String adminNote;
}
