package com.auction.auction_backend.modules.payment.controller;

import com.auction.auction_backend.common.api.BaseResponse;
import com.auction.auction_backend.modules.payment.dto.PaymentResponse;
import com.auction.auction_backend.modules.payment.service.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final StripeService stripeService;
    @PostMapping("/create-payment-intent/{orderId}")
    public ResponseEntity<BaseResponse<PaymentResponse>> createPaymentIntent(@PathVariable Long orderId) {
        return ResponseEntity.ok(BaseResponse.success(stripeService.createPaymentIntent(orderId)));
    }
}
