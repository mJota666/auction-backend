package com.auction.auction_backend.modules.user.service;

import com.auction.auction_backend.modules.user.entity.UpgradeRequest;

import java.util.List;

public interface UpgradeRequestService {
    List<UpgradeRequest> getPendingRequests();

    void approveRequest(Long requestId);

    void rejectRequest(Long requestId, String adminNote);
}
