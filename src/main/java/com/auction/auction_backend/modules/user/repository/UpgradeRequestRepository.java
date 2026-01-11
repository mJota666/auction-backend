package com.auction.auction_backend.modules.user.repository;

import com.auction.auction_backend.common.enums.UpgradeRequestStatus;
import com.auction.auction_backend.modules.user.entity.UpgradeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UpgradeRequestRepository extends JpaRepository<UpgradeRequest, Long> {

    @Query("SELECT r FROM UpgradeRequest r WHERE r.status = :status")
    Page<UpgradeRequest> findByStatus(UpgradeRequestStatus status, Pageable pageable);

    boolean existsByUserIdAndStatus(Long userId, UpgradeRequestStatus status);

    Optional<UpgradeRequest> findByUserIdAndStatus(Long userId, UpgradeRequestStatus status);

    void deleteByUserId(Long userId);
}
