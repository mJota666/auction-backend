package com.auction.auction_backend.modules.bidding.dto.response;

import com.auction.auction_backend.modules.product.dto.response.ProductResponse;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MyBidResponse {
    private Long id;
    private BigDecimal amount;
    private LocalDateTime time;
    private boolean isAutoBid;
    private ProductResponse product;
}
