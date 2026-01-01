package com.auction.auction_backend.modules.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppendDescriptionRequest {
    @NotBlank(message = "Nội dung bổ sung không được để trống")
    private String content;
}
