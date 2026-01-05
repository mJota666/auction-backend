package com.auction.auction_backend.modules.user.service.impl;

import com.auction.auction_backend.common.enums.OrderStatus;
import com.auction.auction_backend.common.exception.AppException;
import com.auction.auction_backend.common.exception.ErrorCode;
import com.auction.auction_backend.modules.order.entity.Order;
import com.auction.auction_backend.modules.order.repository.OrderRepository;
import com.auction.auction_backend.modules.user.dto.request.CreateRatingRequest;
import com.auction.auction_backend.modules.user.dto.response.RatingResponse;
import com.auction.auction_backend.modules.user.entity.Rating;
import com.auction.auction_backend.modules.user.entity.User;
import com.auction.auction_backend.modules.user.repository.RatingRepository;
import com.auction.auction_backend.modules.user.repository.UserRepository;
import com.auction.auction_backend.modules.user.service.RatingService;
import com.auction.auction_backend.security.userdetail.UserPrincipal;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void createRating(CreateRatingRequest request) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        // Validate: User must be part of the order (Winner or Seller)
        boolean isWinner = order.getWinner().getId().equals(currentUser.getId());
        boolean isSeller = order.getSeller().getId().equals(currentUser.getId());

        if (!isWinner && !isSeller) {
            throw new RuntimeException("Bạn không có quyền đánh giá đơn hàng này");
        }

        // Determind who is being rated
        User ratedUser = isWinner ? order.getSeller() : order.getWinner();
        User rater = isWinner ? order.getWinner() : order.getSeller();

        // Validate: Only rate completed orders (or cancelled if seller wants to rate
        // bad buyer)
        // Simplification for MVP: Allow rating if order exists

        Rating rating = Rating.builder()
                .order(order)
                .rater(rater)
                .ratedUser(ratedUser)
                .score(request.getScore())
                .comment(request.getComment())
                .build();

        ratingRepository.save(rating);

        // Update User Reputation
        if (request.getScore() > 0) {
            ratedUser.setRatingPositive(ratedUser.getRatingPositive() + 1);
        } else if (request.getScore() < 0) {
            ratedUser.setRatingNegative(ratedUser.getRatingNegative() + 1);
        }
        userRepository.save(ratedUser);
    }

    @Override
    public Page<RatingResponse> getRatingsReceived(
            Long userId, org.springframework.data.domain.Pageable pageable) {
        return ratingRepository.findByRatedUserId(userId, pageable)
                .map(rating -> com.auction.auction_backend.modules.user.dto.response.RatingResponse.builder()
                        .id(rating.getId())
                        .raterName(rating.getRater().getFullName())
                        .score(rating.getScore())
                        .comment(rating.getComment())
                        .createdAt(rating.getCreatedAt())
                        .build());
    }
}
