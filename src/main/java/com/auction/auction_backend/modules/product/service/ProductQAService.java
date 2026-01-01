package com.auction.auction_backend.modules.product.service;

import com.auction.auction_backend.common.api.PageResponse;
import com.auction.auction_backend.modules.product.dto.request.AnswerQARequest;
import com.auction.auction_backend.modules.product.dto.request.CreateQARequest;
import com.auction.auction_backend.modules.product.dto.response.ProductQAResponse;

public interface ProductQAService {
    void createQuestion(CreateQARequest request);

    void answerQuestion(Long qaId, AnswerQARequest request);

    PageResponse<ProductQAResponse> getProductQAs(Long productId, int page, int size);
}
