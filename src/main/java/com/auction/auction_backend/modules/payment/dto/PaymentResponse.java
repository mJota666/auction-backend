package com.auction.auction_backend.modules.payment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
    private String clientSecret;
    private String transactionId;
}
