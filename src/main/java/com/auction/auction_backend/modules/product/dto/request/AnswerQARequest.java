package com.auction.auction_backend.modules.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnswerQARequest {
    @NotBlank(message = "Câu trả lời không được để trống")
    private String answer;
}
