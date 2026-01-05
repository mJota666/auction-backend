package com.auction.auction_backend.modules.bidding.service.impl;

import com.auction.auction_backend.modules.bidding.dto.request.PlaceBidRequest;

import com.auction.auction_backend.modules.bidding.dto.response.MyBidResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BidService {
    public void placeBid(PlaceBidRequest request);

    public void blockBidder(Long productId, Long userId);

    Page<MyBidResponse> getMyBids(Long userId, Pageable pageable);
}
