package com.auction.auction_backend.modules.user.service.impl;

import com.auction.auction_backend.common.api.PageResponse;
import com.auction.auction_backend.common.enums.UpgradeRequestStatus;
import com.auction.auction_backend.common.enums.UserRole;
import com.auction.auction_backend.common.exception.AppException;
import com.auction.auction_backend.common.exception.ErrorCode;
import com.auction.auction_backend.modules.product.dto.response.ProductResponse;
import com.auction.auction_backend.modules.product.entity.Product;
import com.auction.auction_backend.modules.product.repository.ProductRepository;
import com.auction.auction_backend.modules.user.dto.request.ChangePasswordRequest;
import com.auction.auction_backend.modules.user.dto.request.CreateUpgradeRequest;
import com.auction.auction_backend.modules.user.dto.request.UpdateProfileRequest;
import com.auction.auction_backend.modules.user.dto.response.UserResponse;
import com.auction.auction_backend.modules.user.entity.UpgradeRequest;
import com.auction.auction_backend.modules.user.entity.User;
import com.auction.auction_backend.modules.user.repository.UpgradeRequestRepository;
import com.auction.auction_backend.modules.user.repository.UserRepository;
import com.auction.auction_backend.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final UpgradeRequestRepository upgradeRequestRepository;

    @Override
    public User getProfile(Long userId) {
        return getUserById(userId);
    }

    @Override
    public com.auction.auction_backend.modules.user.dto.response.PublicUserResponse getPublicProfile(Long userId) {
        User user = getUserById(userId);
        return com.auction.auction_backend.modules.user.dto.response.PublicUserResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        User user = getUserById(userId);
        user.setFullName(request.getFullName());
        user.setAddress(request.getAddress());
        user.setDob(request.getDob());

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email đã được sử dụng bởi tài khoản khác");
            }
            user.setEmail(request.getEmail());
        }

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = getUserById(userId);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void toggleFavorite(Long userId, Long productId) {
        User user = getUserById(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Set<Product> favorites = user.getFavoriteProducts();
        if (favorites.contains(product)) {
            favorites.remove(product);
        } else {
            favorites.add(product);
        }
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long productId) {
        User user = getUserById(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Set<Product> favorites = user.getFavoriteProducts();
        if (favorites.contains(product)) {
            favorites.remove(product);
            userRepository.save(user);
        }
    }

    @Override
    public Page<ProductResponse> getFavoriteProducts(Long userId, Pageable pageable) {
        User user = getUserById(userId);
        List<ProductResponse> list = user.getFavoriteProducts().stream()
                .map(ProductResponse::fromEntity)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());

        if (start > list.size()) {
            return new PageImpl<>(List.of(), pageable, list.size());
        }

        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    @Override
    public PageResponse<UserResponse> getUsers(
            String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, size);
        Page<User> pageResult;

        if (keyword != null && !keyword.isBlank()) {
            pageResult = userRepository.findAll(pageable);
        } else {
            pageResult = userRepository.findAll(pageable);
        }

        List<UserResponse> response = pageResult.getContent()
                .stream()
                .map(UserResponse::fromEntity)
                .toList();

        return PageResponse.<UserResponse>builder()
                .content(response)
                .page(pageResult.getNumber() + 1)
                .size(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .build();
    }

    @Override
    @Transactional
    public void banUser(Long userId, boolean ban) {
        User user = getUserById(userId);
        if (user.getRole() == UserRole.ADMIN) {
            throw new RuntimeException("Không thể khóa tài khoản Admin");
        }
        user.setActive(!ban);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void requestUpgrade(Long userId,
            CreateUpgradeRequest request) {
        User user = getUserById(userId);

        if (user.getRole() == UserRole.SELLER ||
                user.getRole() == UserRole.ADMIN) {
            throw new AppException(ErrorCode.USER_ALREADY_SELLER);
        }

        if (upgradeRequestRepository.existsByUserIdAndStatus(userId,
                UpgradeRequestStatus.PENDING)) {
            throw new AppException(ErrorCode.UPGRADE_REQUEST_ALREADY_EXISTS);
        }

        UpgradeRequest upgradeRequest = UpgradeRequest
                .builder()
                .user(user)
                .reason(request.getReason())
                .status(UpgradeRequestStatus.PENDING)
                .build();

        upgradeRequestRepository.save(upgradeRequest);
    }

    @Override
    public UpgradeRequest getMyUpgradeRequest(Long userId) {
        return upgradeRequestRepository.findByUserIdAndStatus(userId,
                UpgradeRequestStatus.PENDING).orElse(null);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
}
