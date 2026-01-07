package com.auction.auction_backend.modules.bidding.controller;

import com.auction.auction_backend.common.api.BaseResponse;
import com.auction.auction_backend.modules.bidding.dto.request.PlaceBidRequest;
import com.auction.auction_backend.modules.bidding.service.impl.BidService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bids")
@RequiredArgsConstructor
public class BidController {
    private final BidService bidService;

    @PostMapping
    public ResponseEntity<BaseResponse<String>> placeBid(@RequestBody @Valid PlaceBidRequest placeBidRequest) {
        bidService.placeBid(placeBidRequest);
        return ResponseEntity.ok(BaseResponse.success("Đặt giá thành công"));
    }

    @PostMapping("/{productId}/block/{userId}")
    public ResponseEntity<BaseResponse<String>> blockBidder(
            @org.springframework.web.bind.annotation.PathVariable Long productId,
            @org.springframework.web.bind.annotation.PathVariable Long userId) {
        bidService.blockBidder(productId, userId);
        return ResponseEntity.ok(BaseResponse.success("Đã chặn người dùng tham gia đấu giá sản phẩm này"));
    }

    @org.springframework.web.bind.annotation.GetMapping("/me")
    public ResponseEntity<BaseResponse<org.springframework.data.domain.Page<com.auction.auction_backend.modules.bidding.dto.response.MyBidResponse>>> getMyBids(
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.auction.auction_backend.security.userdetail.UserPrincipal currentUser,
            org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.success(bidService.getMyBids(currentUser.getId(), pageable)));
    }

    @org.springframework.web.bind.annotation.GetMapping("/product/{productId}")
    public ResponseEntity<BaseResponse<org.springframework.data.domain.Page<com.auction.auction_backend.modules.bidding.dto.response.ProductBidResponse>>> getProductBids(
            @org.springframework.web.bind.annotation.PathVariable Long productId,
            org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.success(bidService.getProductBids(productId, pageable)));
    }
}
