package com.auction.auction_backend.modules.user.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RatingResponse {
    private Long id;
    private String raterName;
    private int score;
    private String comment;
    private LocalDateTime createdAt;
}
