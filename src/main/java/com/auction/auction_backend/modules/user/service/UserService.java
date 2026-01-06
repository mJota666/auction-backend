package com.auction.auction_backend.modules.user.service;

import com.auction.auction_backend.common.api.PageResponse;
import com.auction.auction_backend.modules.product.dto.response.ProductResponse;
import com.auction.auction_backend.modules.user.dto.request.ChangePasswordRequest;
import com.auction.auction_backend.modules.user.dto.request.CreateUpgradeRequest;
import com.auction.auction_backend.modules.user.dto.request.UpdateProfileRequest;
import com.auction.auction_backend.modules.user.dto.response.UserResponse;
import com.auction.auction_backend.modules.user.entity.UpgradeRequest;
import com.auction.auction_backend.modules.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    User getProfile(Long userId);

    void updateProfile(Long userId, UpdateProfileRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);

    void toggleFavorite(Long userId, Long productId);

    void removeFavorite(Long userId, Long productId);

    Page<ProductResponse> getFavoriteProducts(Long userId, Pageable pageable);

    PageResponse<UserResponse> getUsers(
            String keyword, int page, int size);

    void banUser(Long userId, boolean ban);

    void requestUpgrade(Long userId, CreateUpgradeRequest request);

    UpgradeRequest getMyUpgradeRequest(Long userId);
}
