package com.auction.auction_backend.modules.bidding.service.impl;

import com.auction.auction_backend.common.enums.ProductStatus;
import com.auction.auction_backend.common.exception.AppException;
import com.auction.auction_backend.common.exception.ErrorCode;

import com.auction.auction_backend.modules.bidding.dto.request.PlaceBidRequest;
import com.auction.auction_backend.modules.bidding.entity.Bid;
import com.auction.auction_backend.modules.bidding.repository.BidRepository;
import com.auction.auction_backend.modules.product.entity.Product;
import com.auction.auction_backend.modules.product.repository.ProductRepository;
import com.auction.auction_backend.modules.user.entity.User;
import com.auction.auction_backend.modules.user.repository.UserRepository;
import com.auction.auction_backend.security.userdetail.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {
    private final BidRepository bidRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public void placeBid(PlaceBidRequest request) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User bidder = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        validateBid(product, bidder, request.getAmount());
        Bid bid = Bid.builder()
                .product(product)
                .bidder(bidder)
                .bidAmount(request.getAmount())
                .maxAmount(request.getAmount())
                .bidType("MANUAL")
                .build();
        bidRepository.save(bid);

        product.setCurrentPrice(request.getAmount());
        product.setCurrentWinner(bidder);
        productRepository.save(product);
    }

    private void validateBid(Product product, User bidder, BigDecimal bidAmount) {
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new AppException(ErrorCode.BID_AMOUNT_INVALID);
        }
        if (LocalDateTime.now().isAfter(product.getEndAt())) {
            throw new AppException(ErrorCode.AUCTION_ENDED);
        }
        if (product.getSeller().getId().equals(bidder.getId())) {
            throw new AppException(ErrorCode.SELF_BIDDING);
        }
        BigDecimal minValidPrice = product.getCurrentPrice().add(product.getStepPrice());
        if (bidAmount.compareTo(minValidPrice) < 0) {
            throw new AppException(ErrorCode.BID_AMOUNT_INVALID);
        }
    }
}
