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
        List<AutoBidMax> ranking = autoBidRepository.findSortedBids(product.getId());

        if (ranking.isEmpty())
            return;

        if (ranking.size() == 1) {
            AutoBidMax winner = ranking.get(0);
            if (product.getCurrentWinner() == null) {
                updateProduct(product, winner.getBidder(), product.getStartPrice());
            }
            return;
        }

        AutoBidMax top1 = ranking.get(0);
        AutoBidMax top2 = ranking.get(1);

        BigDecimal newPrice = top2.getMaxAmount().add(product.getStepPrice());

        if (newPrice.compareTo(top1.getMaxAmount()) > 0) {
            newPrice = top1.getMaxAmount();
        }

        boolean priceChanged = newPrice.compareTo(product.getCurrentPrice()) != 0;
        boolean winnerChanged = product.getCurrentWinner() == null
                || !product.getCurrentWinner().getId().equals(top1.getBidder().getId());

        if (priceChanged || winnerChanged) {
            Bid bidHistory = Bid.builder()
                    .product(product)
                    .bidder(top1.getBidder())
                    .bidAmount(newPrice)
                    .maxAmount(top1.getMaxAmount())
                    .bidType(com.auction.auction_backend.common.enums.BidType.AUTO)
                    .build();
            bidRepository.save(bidHistory);
            updateProduct(product, top1.getBidder(), newPrice);
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
