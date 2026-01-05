package com.auction.auction_backend.modules.product.service.impl;

import com.auction.auction_backend.modules.bidding.dto.response.BidHistoryResponse;
import com.auction.auction_backend.modules.product.dto.request.CreateProductRequest;
import com.auction.auction_backend.modules.product.dto.request.ProductSearchCriteria;
import com.auction.auction_backend.modules.product.dto.response.ProductDetailResponse;
import com.auction.auction_backend.modules.product.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    void createProduct(CreateProductRequest request);

    public Page<ProductResponse> searchProducts(ProductSearchCriteria criteria);

    List<ProductResponse> getTop5EndingSoon();

    List<ProductResponse> getTop5HighestPrice();

    List<ProductResponse> getTop5MostBids();

    public ProductDetailResponse getProductDetail(Long id);

    public List<BidHistoryResponse> getProductBidHistory(Long id);

    void appendDescription(Long productId, String content);

    void deleteProduct(Long id);
}
