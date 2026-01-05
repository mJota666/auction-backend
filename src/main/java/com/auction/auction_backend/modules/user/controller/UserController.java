package com.auction.auction_backend.modules.user.controller;

import com.auction.auction_backend.common.api.BaseResponse;
import com.auction.auction_backend.modules.product.dto.response.ProductResponse;
import com.auction.auction_backend.modules.user.dto.request.ChangePasswordRequest;
import com.auction.auction_backend.modules.user.dto.request.UpdateProfileRequest;
import com.auction.auction_backend.modules.user.entity.User;
import com.auction.auction_backend.modules.user.service.RatingService;
import com.auction.auction_backend.modules.user.service.UserService;
import com.auction.auction_backend.security.userdetail.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RatingService ratingService;

    @GetMapping("/me")
    public ResponseEntity<BaseResponse<User>> getMyProfile(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(BaseResponse.success(userService.getProfile(currentUser.getId())));
    }

    @PutMapping("/me")
    public ResponseEntity<BaseResponse<String>> updateProfile(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestBody @Valid UpdateProfileRequest request) {
        userService.updateProfile(currentUser.getId(), request);
        return ResponseEntity.ok(BaseResponse.success("Cập nhật thông tin thành công"));
    }

    @PutMapping("/me/password")
    public ResponseEntity<BaseResponse<String>> changePassword(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(currentUser.getId(), request);
        return ResponseEntity.ok(BaseResponse.success("Đổi mật khẩu thành công"));
    }

    @GetMapping("/favorites")
    public ResponseEntity<BaseResponse<Page<ProductResponse>>> getFavorites(
            @AuthenticationPrincipal UserPrincipal currentUser,
            Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.success(userService.getFavoriteProducts(currentUser.getId(), pageable)));
    }

    @PostMapping("/favorites/{productId}")
    public ResponseEntity<BaseResponse<String>> toggleFavorite(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long productId) {
        userService.toggleFavorite(currentUser.getId(), productId);
        return ResponseEntity.ok(BaseResponse.success("Cập nhật danh sách yêu thích thành công"));
    }

    @PostMapping("/favorites")
    public ResponseEntity<BaseResponse<String>> toggleFavoriteBody(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestBody @Valid com.auction.auction_backend.modules.user.dto.request.FavoriteRequest request) {
        userService.toggleFavorite(currentUser.getId(), request.getProductId());
        return ResponseEntity.ok(BaseResponse.success("Cập nhật danh sách yêu thích thành công"));
    }

    @GetMapping("/me/ratings")
    public ResponseEntity<BaseResponse<Page<com.auction.auction_backend.modules.user.dto.response.RatingResponse>>> getMyRatings(
            @AuthenticationPrincipal UserPrincipal currentUser,
            Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.success(ratingService.getRatingsReceived(currentUser.getId(), pageable)));
    }
}
