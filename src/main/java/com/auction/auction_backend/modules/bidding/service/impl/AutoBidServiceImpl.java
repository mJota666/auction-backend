package com.auction.auction_backend.modules.bidding.service.impl;

import com.auction.auction_backend.modules.bidding.entity.AutoBidMax;
import com.auction.auction_backend.modules.bidding.entity.Bid;
import com.auction.auction_backend.modules.bidding.repository.AutoBidRepository;
import com.auction.auction_backend.modules.bidding.repository.BidRepository;
import com.auction.auction_backend.modules.notification.service.NotificationService;
import com.auction.auction_backend.modules.product.entity.Product;
import com.auction.auction_backend.modules.product.repository.ProductRepository;
import com.auction.auction_backend.modules.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoBidServiceImpl implements AutoBidService {
    private final AutoBidRepository autoBidRepository;
    private final ProductRepository productRepository;
    private final BidRepository bidRepository;
    private final NotificationService notificationService;
    private final com.auction.auction_backend.modules.notification.service.EmailService emailService;

    @Override
    @Transactional
    public void triggerAutoBid(Product product) {
        log.info("[DEBUG_BID] triggerAutoBid START for ProductId: {}", product.getId());
        List<AutoBidMax> ranking = autoBidRepository.findSortedBids(product.getId());
        log.info("[DEBUG_BID] Ranking Size: {}", ranking.size());

        if (ranking.isEmpty()) {
            log.info("[DEBUG_BID] Ranking is empty, returning.");
            return;
        }

        for (int i = 0; i < ranking.size(); i++) {
            log.info("[DEBUG_BID] Rank {}: User {} - Max {}", i, ranking.get(i).getBidder().getEmail(),
                    ranking.get(i).getMaxAmount());
        }

        if (ranking.size() == 1) {
            AutoBidMax winner = ranking.get(0);
            log.info("[DEBUG_BID] Single bidder case: {}", winner.getBidder().getEmail());
            if (product.getCurrentWinner() == null) {
                log.info("[DEBUG_BID] Setting initial winner: {} at StartPrice: {}", winner.getBidder().getEmail(),
                        product.getStartPrice());
                updateProduct(product, winner.getBidder(), product.getStartPrice());
            } else {
                log.info("[DEBUG_BID] Winner already exists (User: {}). No change in price.",
                        product.getCurrentWinner().getEmail());
            }
            return;
        }

        AutoBidMax top1 = ranking.get(0);
        AutoBidMax top2 = ranking.get(1);
        log.info("[DEBUG_BID] Top1: {} (Max: {}) | Top2: {} (Max: {})",
                top1.getBidder().getEmail(), top1.getMaxAmount(),
                top2.getBidder().getEmail(), top2.getMaxAmount());

        BigDecimal newPrice = top2.getMaxAmount().add(product.getStepPrice());
        log.info("[DEBUG_BID] Calculated New Price (Top2 + Step): {}", newPrice);

        if (newPrice.compareTo(top1.getMaxAmount()) > 0) {
            newPrice = top1.getMaxAmount();
            log.info("[DEBUG_BID] New Price capped at Top1 Max: {}", newPrice);
        }

        log.info("[DEBUG_BID] Current Price: {}", product.getCurrentPrice());
        boolean priceChanged = newPrice.compareTo(product.getCurrentPrice()) != 0;
        boolean winnerChanged = product.getCurrentWinner() == null
                || !product.getCurrentWinner().getId().equals(top1.getBidder().getId());

        log.info("[DEBUG_BID] PriceChanged: {} | WinnerChanged: {}", priceChanged, winnerChanged);

        if (priceChanged || winnerChanged) {
            Bid bidHistory = Bid.builder()
                    .product(product)
                    .bidder(top1.getBidder())
                    .bidAmount(newPrice)
                    .maxAmount(top1.getMaxAmount())
                    .bidType(com.auction.auction_backend.common.enums.BidType.AUTO)
                    .build();
            bidRepository.save(bidHistory);
            log.info("[DEBUG_BID] Saved Bid History: Amount {}", newPrice);
            updateProduct(product, top1.getBidder(), newPrice);
        } else {
            log.info("[DEBUG_BID] No change in price or winner. Nothing processed.");
        }
    }

    private void updateProduct(Product product, User newWinner, BigDecimal newPrice) {
        User oldWinner = product.getCurrentWinner();
        String productLink = "http://localhost:5173/products/" + product.getId(); // TODO: Use config

        // 1. Notify Old Winner (Outbid)
        if (oldWinner != null && !oldWinner.getId().equals(newWinner.getId())) {
            try {
                // In-app notification
                notificationService.sendNotification(
                        oldWinner,
                        "Bạn đã bị vượt mặt!",
                        "Sản phẩm '" + product.getTitle() + "' vừa có người đặt giá cao hơn: " + newPrice,
                        "/products/" + product.getId(),
                        "OUTBID");

                // Email notification
                emailService.sendOutbidNotification(
                        oldWinner.getEmail(),
                        product.getTitle(),
                        newPrice,
                        productLink);
            } catch (Exception e) {
                log.error("Lỗi gửi thông báo outbid: {}", e.getMessage());
            }
        }

        // 2. Notify New Winner (Bid Success)
        // Avoid sending if new winner is same as old winner (e.g. auto bid increment) -
        // though requirement says "Confirm bid".
        // Depending on UX, we might want to notify every time price increases or just
        // when they become winner.
        // Let's notify every time price confirms for them.
        try {
            emailService.sendBidPlacedNotification(
                    newWinner.getEmail(),
                    product.getTitle(),
                    newPrice,
                    productLink,
                    false);
        } catch (Exception e) {
            log.error("Lỗi gửi email bid success: {}", e.getMessage());
        }

        // 3. Notify Seller (New Bid)
        try {
            emailService.sendBidPlacedNotification(
                    product.getSeller().getEmail(),
                    product.getTitle(),
                    newPrice,
                    productLink,
                    true);
        } catch (Exception e) {
            log.error("Lỗi gửi email seller: {}", e.getMessage());
        }

        product.setCurrentPrice(newPrice);
        product.setCurrentWinner(newWinner);
        productRepository.save(product);
        log.info("Product {} - New Price: {} - Winner: {}", product.getId(), newPrice, newWinner.getEmail());
    }
}
