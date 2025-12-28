package com.auction.auction_backend.modules.product.service.impl;

import com.auction.auction_backend.modules.product.dto.request.CreateProductRequest;
import com.auction.auction_backend.modules.product.dto.request.ProductSearchCriteria;
import com.auction.auction_backend.modules.product.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

public interface ProductService {
    void createProduct(CreateProductRequest request);
    public Page<ProductResponse> searchProducts(ProductSearchCriteria criteria);
}
