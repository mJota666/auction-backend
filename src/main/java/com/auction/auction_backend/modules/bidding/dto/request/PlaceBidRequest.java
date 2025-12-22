package com.auction.auction_backend.modules.bidding.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlaceBidRequest {
    @NotNull(message = "Sản phẩm không hợp lệ")
    private Long productId;

    @NotNull(message = "Giá đặt không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá đặt phải hơn 0")
    private BigDecimal amount;
}
