package com.auction.auction_backend.modules.bidding.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductBidResponse {
    private Long id;
    private Long bidderId;
    private BigDecimal amount;
    private LocalDateTime time;
    private String bidderName; // Masked
    private int ratingPositive;
    private int ratingNegative;
}
