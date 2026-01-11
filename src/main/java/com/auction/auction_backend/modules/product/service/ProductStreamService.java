package com.auction.auction_backend.modules.product.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class ProductStreamService {

    private final Map<Long, List<SseEmitter>> productEmitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long productId) {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1 hour timeout

        productEmitters.computeIfAbsent(productId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        log.info("Client subscribed to product stream: {}", productId);

        Runnable removeEmitter = () -> {
            List<SseEmitter> emitters = productEmitters.get(productId);
            if (emitters != null) {
                emitters.remove(emitter);
                if (emitters.isEmpty()) {
                    productEmitters.remove(productId);
                }
            }
            log.info("Client disconnected from product stream: {}", productId);
        };

        emitter.onCompletion(removeEmitter);
        emitter.onTimeout(removeEmitter);
        emitter.onError((e) -> removeEmitter.run());

        return emitter;
    }

    public void broadcastProductUpdate(Long productId, BigDecimal currentPrice, String winnerName) {
        List<SseEmitter> emitters = productEmitters.get(productId);
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        ProductUpdateEvent event = new ProductUpdateEvent(productId, currentPrice, winnerName);
        log.info("Broadcasting update to {} clients for product {}", emitters.size(), productId);

        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("product-update").data(event));
            } catch (Exception e) {
                // Catching generic Exception to handle ClientAbortException (which might be
                // wrapped) and others
                log.debug("Unsubscribe client due to error: {}", e.getMessage());
                emitter.complete();
                productEmitters.get(productId).remove(emitter); // Ensure removal
            }
        });
    }

    // Inner class for payload
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class ProductUpdateEvent {
        private Long productId;
        private BigDecimal currentPrice;
        private String currentWinner;
    }
}
