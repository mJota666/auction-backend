package com.auction.auction_backend.modules.user.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRatingRequest {
    @NotNull(message = "Order ID không được để trống")
    private Long orderId;

    @Min(value = -1, message = "Điểm đánh giá không hợp lệ")
    @Max(value = 1, message = "Điểm đánh giá không hợp lệ")
    private int score; // -1 hoặc 1

    private String comment;
}
