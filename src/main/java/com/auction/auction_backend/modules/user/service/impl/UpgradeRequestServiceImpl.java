package com.auction.auction_backend.modules.user.service.impl;

import com.auction.auction_backend.common.api.PageResponse;
import com.auction.auction_backend.common.enums.UpgradeRequestStatus;
import com.auction.auction_backend.common.enums.UserRole;
import com.auction.auction_backend.common.exception.AppException;
import com.auction.auction_backend.common.exception.ErrorCode;
import com.auction.auction_backend.modules.user.dto.request.ProcessUpgradeRequest;
import com.auction.auction_backend.modules.user.dto.request.RequestUpgradeRequest;
import com.auction.auction_backend.modules.user.dto.response.UpgradeRequestResponse;
import com.auction.auction_backend.modules.user.entity.UpgradeRequest;
import com.auction.auction_backend.modules.user.entity.User;
import com.auction.auction_backend.modules.user.repository.UpgradeRequestRepository;
import com.auction.auction_backend.modules.user.repository.UserRepository;
import com.auction.auction_backend.modules.user.service.UpgradeRequestService;
import com.auction.auction_backend.security.userdetail.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpgradeRequestServiceImpl implements UpgradeRequestService {

    private final UpgradeRequestRepository upgradeRequestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void requestUpgrade(RequestUpgradeRequest request) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() == UserRole.SELLER) {
            throw new RuntimeException("Bạn đã là người bán hàng");
        }

        if (upgradeRequestRepository.findByUserIdAndStatus(user.getId(), UpgradeRequestStatus.PENDING).isPresent()) {
            throw new RuntimeException("Bạn đã có yêu cầu đang chờ duyệt");
        }

        UpgradeRequest upgradeRequest = UpgradeRequest.builder()
                .user(user)
                .reason(request.getReason())
                .status(UpgradeRequestStatus.PENDING)
                .build();

        upgradeRequestRepository.save(upgradeRequest);
    }

    @Override
    public PageResponse<UpgradeRequestResponse> getRequestsByStatus(UpgradeRequestStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, size);
        Page<UpgradeRequest> pageResult;

        if (status != null) {
            pageResult = upgradeRequestRepository.findByStatus(status, pageable);
        } else {
            pageResult = upgradeRequestRepository.findAll(pageable);
        }

        List<UpgradeRequestResponse> response = pageResult.getContent().stream()
                .map(UpgradeRequestResponse::fromEntity)
                .toList();

        return PageResponse.<UpgradeRequestResponse>builder()
                .content(response)
                .page(pageResult.getNumber() + 1)
                .size(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .build();
    }

    @Override
    @Transactional
    public void processRequest(Long requestId, ProcessUpgradeRequest request) {
        UpgradeRequest upgradeRequest = upgradeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Yêu cầu không tồn tại"));

        if (upgradeRequest.getStatus() != UpgradeRequestStatus.PENDING) {
            throw new RuntimeException("Yêu cầu này đã được xử lý");
        }

        upgradeRequest.setStatus(request.getStatus());
        upgradeRequest.setAdminNote(request.getAdminNote());

        if (request.getStatus() == UpgradeRequestStatus.APPROVED) {
            User user = upgradeRequest.getUser();
            user.setRole(UserRole.SELLER);
            userRepository.save(user);
        }

        upgradeRequestRepository.save(upgradeRequest);
    }
}
