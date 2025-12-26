package com.auction.auction_backend.modules.bidding.service.impl;

import com.auction.auction_backend.modules.product.entity.Product;

public interface AutoBidService {
    public void triggerAutoBid(Product product);
}
