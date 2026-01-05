package com.auction.auction_backend.modules.bidding.service.impl;

import com.auction.auction_backend.modules.bidding.dto.request.PlaceBidRequest;

public interface BidService {
    public void placeBid(PlaceBidRequest request);

    public void blockBidder(Long productId, Long userId);
}
