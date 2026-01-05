package com.auction.auction_backend.modules.user.service;

import com.auction.auction_backend.modules.user.dto.request.CreateRatingRequest;
import com.auction.auction_backend.modules.user.dto.response.RatingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RatingService {
    void createRating(CreateRatingRequest request);

    Page<RatingResponse> getRatingsReceived(Long userId, Pageable pageable);
}
