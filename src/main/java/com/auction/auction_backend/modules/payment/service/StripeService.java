package com.auction.auction_backend.modules.payment.service;

import com.auction.auction_backend.common.exception.AppException;
import com.auction.auction_backend.common.exception.ErrorCode;
import com.auction.auction_backend.modules.order.entity.Order;
import com.auction.auction_backend.modules.order.repository.OrderRepository;
import com.auction.auction_backend.modules.payment.dto.PaymentResponse;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeService {
    @Value("${stripe.api-key}")
    private String apiKey;

    private final OrderRepository orderRepository;

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }

    public PaymentResponse createPaymentIntent(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        long amount = order.getFinalPrice().longValue();
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency("vnd")
                .putMetadata("order_id", order.getId().toString())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()
                )
                .build();
        try {
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            return PaymentResponse.builder()
                    .clientSecret(paymentIntent.getClientSecret())
                    .transactionId(paymentIntent.getId())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo thanh toán Stripe: " + e.getMessage());
        }
    }
}
