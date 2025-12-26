package com.auction.auction_backend.modules.order.service.impl;

import com.auction.auction_backend.modules.product.entity.Product;

public interface OrderService {
    public void createOrderFromAuction(Product product);
}
