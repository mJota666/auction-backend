package com.auction.auction_backend.modules.user.controller;

import com.auction.auction_backend.common.api.BaseResponse;
import com.auction.auction_backend.modules.user.dto.request.CreateRatingRequest;
import com.auction.auction_backend.modules.user.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<BaseResponse<String>> createRating(@RequestBody @Valid CreateRatingRequest request) {
        ratingService.createRating(request);
        return ResponseEntity.ok(BaseResponse.success("Đánh giá thành công"));
    }

    @org.springframework.web.bind.annotation.GetMapping("/my-ratings")
    public ResponseEntity<BaseResponse<org.springframework.data.domain.Page<com.auction.auction_backend.modules.user.dto.response.RatingResponse>>> getMyRatings(
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.auction.auction_backend.security.userdetail.UserPrincipal currentUser,
            org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.success(ratingService.getRatingsReceived(currentUser.getId(), pageable)));
    }
}
