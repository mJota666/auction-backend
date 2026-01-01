package com.auction.auction_backend.modules.admin.service;

import com.auction.auction_backend.modules.admin.dto.response.DashboardResponse;

public interface DashboardService {
    DashboardResponse getDashboardStats();
}
