package com.auction.auction_backend.modules.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestUpgradeRequest {
    @NotBlank(message = "Lý do không được để trống")
    private String reason;
}
