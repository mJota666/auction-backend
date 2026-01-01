package com.auction.auction_backend.modules.admin.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardResponse {
    private long totalUsers;
    private long totalProducts;
    private long activeAuctions;
    private BigDecimal totalRevenue;
}
