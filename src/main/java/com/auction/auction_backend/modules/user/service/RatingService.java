package com.auction.auction_backend.modules.user.service;

import com.auction.auction_backend.modules.user.dto.request.CreateRatingRequest;

public interface RatingService {
    void createRating(CreateRatingRequest request);
}
