package com.auction.auction_backend.modules.user.service.impl;

import com.auction.auction_backend.common.enums.UpgradeRequestStatus;
import com.auction.auction_backend.common.enums.UserRole;
import com.auction.auction_backend.modules.user.entity.UpgradeRequest;
import com.auction.auction_backend.modules.user.entity.User;
import com.auction.auction_backend.modules.user.repository.UpgradeRequestRepository;
import com.auction.auction_backend.modules.user.repository.UserRepository;
import com.auction.auction_backend.modules.user.service.UpgradeRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpgradeRequestServiceImpl implements UpgradeRequestService {

    private final UpgradeRequestRepository upgradeRequestRepository;
    private final UserRepository userRepository;

    @Override
    public List<UpgradeRequest> getPendingRequests() {
        return upgradeRequestRepository.findByStatus(UpgradeRequestStatus.PENDING, Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional
    public void approveRequest(Long requestId) {
        UpgradeRequest request = upgradeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Yêu cầu không tồn tại"));

        if (request.getStatus() != UpgradeRequestStatus.PENDING) {
            throw new RuntimeException("Yêu cầu đã được xử lý");
        }

        request.setStatus(UpgradeRequestStatus.APPROVED);
        upgradeRequestRepository.save(request);

        // Upgrade user role
        User user = request.getUser();
        user.setRole(UserRole.SELLER);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void rejectRequest(Long requestId, String adminNote) {
        UpgradeRequest request = upgradeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Yêu cầu không tồn tại"));

        if (request.getStatus() != UpgradeRequestStatus.PENDING) {
            throw new RuntimeException("Yêu cầu đã được xử lý");
        }

        request.setStatus(UpgradeRequestStatus.REJECTED);
        request.setAdminNote(adminNote);
        upgradeRequestRepository.save(request);
    }
}
