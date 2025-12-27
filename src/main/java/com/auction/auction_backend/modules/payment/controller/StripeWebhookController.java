package com.auction.auction_backend.modules.payment.controller;

import com.auction.auction_backend.common.enums.OrderStatus;
import com.auction.auction_backend.common.enums.PaymentMethod;
import com.auction.auction_backend.modules.order.entity.Order;
import com.auction.auction_backend.modules.order.repository.OrderRepository;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/webhook/stripe")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {
    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    private final OrderRepository orderRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<String> handleStripeEvent(@RequestBody String payload,
                                                    @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            log.info("Evene ne:", event);
        } catch(Exception e) {
            log.error("Webhook signature verification failed", e);
            return ResponseEntity.badRequest().build();
        }
        log.info("event type ne:", event.getType());
        if ("payment_intent.succeeded".equals(event.getType())) {
            PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
            if (paymentIntent != null) {
                String orderIdStr = paymentIntent.getMetadata().get("order_id");
                log.info("Thanh toán thành công cho Order ID: {}", orderIdStr);

                Long orderId = Long.parseLong(orderIdStr);
                Order order = orderRepository.findById(orderId).orElse(null);

                if (order != null && order.getStatus() == OrderStatus.PENDING_PAYMENT) {
                    order.setStatus(OrderStatus.PAID);
                    order.setPaymentMethod(PaymentMethod.STRIPE);
                    order.setPaidAt(LocalDateTime.now());
                    order.setPaymentRefId(paymentIntent.getId());

                    orderRepository.save(order);
                }
            }
        }
        return ResponseEntity.ok("Received");
    }
}
