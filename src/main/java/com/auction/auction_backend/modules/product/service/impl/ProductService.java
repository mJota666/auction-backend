package com.auction.auction_backend.modules.product.service.impl;

import com.auction.auction_backend.modules.product.dto.request.CreateProductRequest;

public interface ProductService {
    void createProduct(CreateProductRequest request);
}
