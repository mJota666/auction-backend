package com.auction.auction_backend.modules.bidding.service.impl;

import com.auction.auction_backend.modules.bidding.entity.AutoBidMax;
import com.auction.auction_backend.modules.bidding.entity.Bid;
import com.auction.auction_backend.modules.bidding.repository.AutoBidRepository;
import com.auction.auction_backend.modules.bidding.repository.BidRepository;
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
    @Override
    @Transactional
    public void triggerAutoBid(Product product) {
        List<AutoBidMax> ranking = autoBidRepository.findSortedBids(product.getId());

        if (ranking.isEmpty()) return;

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
        boolean winnerChanged = product.getCurrentWinner() == null || !product.getCurrentWinner().getId().equals(top1.getBidder().getId());

        if (priceChanged || winnerChanged) {
            Bid bidHistory = Bid.builder()
                    .product(product)
                    .bidder(top1.getBidder())
                    .bidAmount(newPrice)
                    .maxAmount(top1.getMaxAmount())
                    .bidType("AUTO")
                    .build();
            bidRepository.save(bidHistory);
            updateProduct(product, top1.getBidder(), newPrice);
        }
    }

    private void updateProduct(Product product, User winner, BigDecimal price) {
        product.setCurrentPrice(price);
        product.setCurrentWinner(winner);
        productRepository.save(product);
        log.info("Product {} - New Price: {} - Winner: {}", product.getId(), price, winner.getEmail());
    }
}
