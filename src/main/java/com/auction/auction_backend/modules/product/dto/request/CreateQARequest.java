package com.auction.auction_backend.modules.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateQARequest {
    @NotNull(message = "Product ID không được để trống")
    private Long productId;

    @NotBlank(message = "Câu hỏi không được để trống")
    private String question;
}
