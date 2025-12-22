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
}
