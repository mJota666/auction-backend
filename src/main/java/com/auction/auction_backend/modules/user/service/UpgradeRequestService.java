package com.auction.auction_backend.modules.user.service;

import com.auction.auction_backend.common.api.PageResponse;
import com.auction.auction_backend.common.enums.UpgradeRequestStatus;
import com.auction.auction_backend.modules.user.dto.request.ProcessUpgradeRequest;
import com.auction.auction_backend.modules.user.dto.request.RequestUpgradeRequest;
import com.auction.auction_backend.modules.user.dto.response.UpgradeRequestResponse;

public interface UpgradeRequestService {
    void requestUpgrade(RequestUpgradeRequest request);

    PageResponse<UpgradeRequestResponse> getRequestsByStatus(UpgradeRequestStatus status, int page, int size);

    void processRequest(Long requestId, ProcessUpgradeRequest request);
}
