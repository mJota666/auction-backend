package com.auction.auction_backend.modules.bidding.dto.response;

import com.auction.auction_backend.modules.bidding.entity.Bid;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BidHistoryResponse {
    private Long id;
    private String bidderName;
    private Long bidderId;
    private BigDecimal amount;
    private LocalDateTime time;
    private boolean isAutoBid;

    public static BidHistoryResponse fromEntity(Bid bid, boolean isSeller) {
        return BidHistoryResponse.builder()
                .id(bid.getId())
                .bidderName(isSeller ? bid.getBidder().getFullName() : maskName(bid.getBidder().getFullName()))
                .bidderId(isSeller ? bid.getBidder().getId() : null)
                .amount(bid.getBidAmount())
                .time(bid.getCreatedAt())
                .isAutoBid("AUTO".equals(bid.getBidType()))
                .build();
    }

    private static String maskName(String fullName) {
        if (fullName == null || fullName.isEmpty())
            return "****Unknown";

        String[] parts = fullName.split(" ");
        if (parts.length == 1) {
            return "****" + parts[0];
        }

        StringBuilder masked = new StringBuilder("****");
        for (int i = 1; i < parts.length; i++) {
            masked.append(parts[i]).append(" ");
        }
        return masked.toString().trim();
    }
}
