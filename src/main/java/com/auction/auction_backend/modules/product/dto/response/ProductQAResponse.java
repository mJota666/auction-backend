package com.auction.auction_backend.modules.product.dto.response;

import com.auction.auction_backend.modules.product.entity.ProductQA;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProductQAResponse {
    private Long id;
    private Long productId;
    private Long askerId;
    private String askerName;
    private String question;
    private String answer;
    private boolean isAnswered;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductQAResponse fromEntity(ProductQA qa) {
        return ProductQAResponse.builder()
                .id(qa.getId())
                .productId(qa.getProduct().getId())
                .askerId(qa.getAsker().getId())
                .askerName(qa.getAsker().getFullName())
                .question(qa.getQuestion())
                .answer(qa.getAnswer())
                .isAnswered(qa.isAnswered())
                .createdAt(qa.getCreatedAt() != null ? qa.getCreatedAt() : LocalDateTime.now())
                .updatedAt(qa.getUpdatedAt())
                .build();
    }
}
