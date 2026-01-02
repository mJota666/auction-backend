package com.auction.auction_backend.modules.bidding.service.impl;

import com.auction.auction_backend.common.enums.ProductStatus;
import com.auction.auction_backend.common.exception.AppException;
import com.auction.auction_backend.common.exception.ErrorCode;

import com.auction.auction_backend.modules.bidding.dto.request.PlaceBidRequest;
import com.auction.auction_backend.modules.bidding.entity.AutoBidMax;
import com.auction.auction_backend.modules.bidding.entity.Bid;
import com.auction.auction_backend.modules.bidding.repository.AutoBidRepository;
import com.auction.auction_backend.modules.bidding.repository.BidRepository;
import com.auction.auction_backend.modules.product.entity.Product;
import com.auction.auction_backend.modules.product.repository.ProductRepository;
import com.auction.auction_backend.modules.user.entity.User;
import com.auction.auction_backend.modules.user.repository.UserRepository;
import com.auction.auction_backend.security.userdetail.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AutoBidRepository autoBidRepository;
    private final AutoBidService autoBidService;

    @Override
    @Transactional
    public void placeBid(PlaceBidRequest request) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User bidder = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        validateBid(product, bidder, request.getAmount());

        AutoBidMax autoBid = autoBidRepository.findByProductAndBidder(product, bidder)
                .orElse(AutoBidMax.builder()
                        .product(product)
                        .bidder(bidder)
                        .maxAmount(BigDecimal.ZERO)
                        .build());

        if (request.getAmount().compareTo(autoBid.getMaxAmount()) > 0) {
            autoBid.setMaxAmount(request.getAmount());
            autoBidRepository.save(autoBid);
        }

        autoBidService.triggerAutoBid(product);
    }

    private void validateBid(Product product, User bidder, BigDecimal inputMaxAmount) {
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new AppException(ErrorCode.AUCTION_ENDED);
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(product.getEndAt())) {
            throw new AppException(ErrorCode.AUCTION_ENDED);
        }
        if (now.isBefore(product.getStartAt())) {
            throw new RuntimeException("Phiên đấu giá chưa bắt đầu");
        }

        if (product.getSeller().getId().equals(bidder.getId())) {
            throw new AppException(ErrorCode.SELF_BIDDING);
        }

        // Check Rating Point (Block if < 80%)
        // Only check if user has at least some ratings to avoid blocking new users
        int totalRatings = bidder.getRatingPositive() + bidder.getRatingNegative();
        if (totalRatings > 0) {
            double ratingRatio = (double) bidder.getRatingPositive() / totalRatings;
            if (ratingRatio < 0.8) {
                throw new RuntimeException("Điểm tín nhiệm của bạn quá thấp (< 80%) để tham gia đấu giá.");
            }
        } else {
            if (!product.isAllowUnratedBidder()) {
                throw new RuntimeException("Người bán không cho phép tài khoản chưa có đánh giá tham gia.");
            }
        }

        BigDecimal minValidPrice = product.getCurrentPrice().add(product.getStepPrice());
        if (inputMaxAmount.compareTo(minValidPrice) < 0) {
            throw new AppException(ErrorCode.BID_AMOUNT_INVALID);
        }
    }
}
