package com.auction.auction_backend.scheduling;

import com.auction.auction_backend.modules.order.service.impl.OrderService;
import com.auction.auction_backend.modules.product.entity.Product;
import com.auction.auction_backend.modules.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionScheduler {
    private final ProductRepository productRepository;
    private final OrderService orderService;

    @Scheduled(fixedRate = 60000, initialDelay = 10000)
    @Transactional
    public void scanExpiredAuctions() {

        log.info("Scanning for expired auctions...");
        List<Product> expiredProducts = productRepository.findExpiredAuctions(LocalDateTime.now());
        if (expiredProducts.isEmpty()) return;
        log.info("Found {} expired auctions.", expiredProducts.size());
        for (Product product : expiredProducts) {
            try {
                orderService.createOrderFromAuction(product);
            } catch (Exception e) {
                log.error("Failed to process auction for product {}: {}", product.getId(), e.getMessage());
            }
        }
    }
}
