package com.auction.auction_backend.modules.bidding.service.impl;

import com.auction.auction_backend.common.enums.ProductStatus;
import com.auction.auction_backend.common.exception.AppException;
import com.auction.auction_backend.common.exception.ErrorCode;

import com.auction.auction_backend.modules.bidding.dto.request.PlaceBidRequest;
import com.auction.auction_backend.modules.bidding.entity.AutoBidMax;
import com.auction.auction_backend.modules.bidding.repository.AutoBidRepository;
import com.auction.auction_backend.modules.bidding.repository.BidRepository;
import com.auction.auction_backend.modules.bidding.repository.BlockedBidderRepository;
import com.auction.auction_backend.modules.product.entity.Product;
import com.auction.auction_backend.modules.product.repository.ProductRepository;
import com.auction.auction_backend.modules.user.entity.User;
import com.auction.auction_backend.modules.user.repository.UserRepository;
import com.auction.auction_backend.security.userdetail.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.auction.auction_backend.modules.notification.service.EmailService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BidServiceImpl implements BidService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AutoBidRepository autoBidRepository;
    private final AutoBidService autoBidService;
    private final BlockedBidderRepository blockedBidderRepository;
    private final BidRepository bidRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public void placeBid(PlaceBidRequest request) {
        log.info("[DEBUG_BID] placeBid called for ProductId: {}, Amount: {}", request.getProductId(),
                request.getAmount());
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User bidder = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Product product = productRepository.findByIdWithLock(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        validateBid(product, bidder, request.getAmount());

        AutoBidMax autoBid = autoBidRepository.findByProductAndBidder(product, bidder)
                .orElse(AutoBidMax.builder()
                        .product(product)
                        .bidder(bidder)
                        .maxAmount(BigDecimal.ZERO)
                        .build());

        log.info("[DEBUG_BID] Current AutoBid Max for User {}: {}", bidder.getEmail(), autoBid.getMaxAmount());

        if (request.getAmount().compareTo(autoBid.getMaxAmount()) > 0) {
            log.info("[DEBUG_BID] Updating AutoBid Max to {}", request.getAmount());
            autoBid.setMaxAmount(request.getAmount());
            autoBidRepository.saveAndFlush(autoBid);
            log.info("[DEBUG_BID] AutoBid saved");
        } else {
            log.info("[DEBUG_BID] Input amount {} is not greater than current Max {}, skipping save",
                    request.getAmount(), autoBid.getMaxAmount());
        }

        // Auto Extension Logic
        if (product.isAutoExtendEnabled() &&
                product.getEndAt().isBefore(java.time.LocalDateTime.now().plusMinutes(5))) {
            product.setEndAt(product.getEndAt().plusMinutes(10));
            productRepository.save(product);
        }

        log.info("[DEBUG_BID] Calling triggerAutoBid");
        autoBidService.triggerAutoBid(product);
    }

    @Override
    @Transactional
    public void blockBidder(Long productId, Long userId) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!product.getSeller().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không phải là người bán của sản phẩm này");
        }

        User bidder = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (blockedBidderRepository.existsByProductIdAndUserId(productId, userId)) {
            throw new RuntimeException("Người dùng đã bị chặn");
        }

        com.auction.auction_backend.modules.bidding.entity.BlockedBidder blocked = com.auction.auction_backend.modules.bidding.entity.BlockedBidder
                .builder()
                .product(product)
                .user(bidder)
                .build();
        blockedBidderRepository.save(blocked);

        // 1. Remove existing auto bids configuration
        autoBidRepository.deleteByProductAndBidder(product, bidder);
        // 2. Remove existing placed bids history
        bidRepository.deleteByProductAndBidder(product, bidder);
        bidRepository.flush();

        // 3. If blocked user was the current winner, clear winner temporarily
        if (product.getCurrentWinner() != null && product.getCurrentWinner().getId().equals(userId)) {
            product.setCurrentWinner(null);
            productRepository.save(product);
        }

        // 4. Check if there are any remaining bidders
        java.util.List<com.auction.auction_backend.modules.bidding.entity.AutoBidMax> remainingBidders = autoBidRepository
                .findSortedBids(productId);

        if (remainingBidders.isEmpty()) {
            // No one left, reset to start price
            product.setCurrentPrice(product.getStartPrice());
            product.setCurrentWinner(null);
            productRepository.save(product);
        } else {
            // Retrigger calculation to pick next winner (fallback)
            autoBidService.triggerAutoBid(product);
        }

        // Notify blocked user
        try {
            String productLink = "http://localhost:5173/products/" + product.getId(); // TODO: config
            emailService.sendBidderBlockedNotification(
                    bidder.getEmail(),
                    product.getTitle(),
                    product.getSeller().getFullName(),
                    productLink);
        } catch (Exception e) {
            // log
        }
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

        if (blockedBidderRepository.existsByProductIdAndUserId(product.getId(), bidder.getId())) {
            throw new RuntimeException("Bạn đã bị người bán chặn tham gia đấu giá sản phẩm này");
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

    @Override
    public Page<com.auction.auction_backend.modules.bidding.dto.response.MyBidResponse> getMyBids(
            Long userId, Pageable pageable) {
        return bidRepository.findByBidderId(userId, pageable)
                .map(bid -> com.auction.auction_backend.modules.bidding.dto.response.MyBidResponse.builder()
                        .id(bid.getId())
                        .amount(bid.getBidAmount())
                        .time(bid.getCreatedAt())
                        .isAutoBid("AUTO".equals(bid.getBidType()))
                        .product(com.auction.auction_backend.modules.product.dto.response.ProductResponse
                                .fromEntity(bid.getProduct()))
                        .build());
    }

    @Override
    public Page<com.auction.auction_backend.modules.bidding.dto.response.ProductBidResponse> getProductBids(
            Long productId, Pageable pageable) {

        return bidRepository.findByProductId(productId, pageable)
                .map(bid -> com.auction.auction_backend.modules.bidding.dto.response.ProductBidResponse.builder()
                        .id(bid.getId())
                        .bidderId(bid.getBidder().getId())
                        .amount(bid.getBidAmount())
                        .time(bid.getCreatedAt())
                        .bidderName(com.auction.auction_backend.common.utils.AppUtils
                                .maskName(bid.getBidder().getFullName()))
                        .ratingPositive(bid.getBidder().getRatingPositive())
                        .ratingNegative(bid.getBidder().getRatingNegative())
                        .build());
    }
}
